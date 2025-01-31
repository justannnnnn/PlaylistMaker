package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor
): ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    private var timerJob: Job? = null

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPause(){
        pausePlayer()
    }

    fun onPlayButtonClicked(){
        when (playerStateLiveData.value){
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun onFavoriteClicked(track: Track){
        when (playerStateLiveData.value?.isFavorite){
            true -> favoritesInteractor.deleteTrackFromFavorites(track)
            false -> favoritesInteractor.addTrackToFavorites(track)
            null -> {}
        }
        playerStateLiveData.postValue(playerStateLiveData.value.co)
    }

    fun preparePlayer(url: String){
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer(){
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default()
    }

    private fun startTimer(){
        timerJob = viewModelScope.launch{
            while (mediaPlayer.isPlaying){
                delay(300L)
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }




}