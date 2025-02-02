package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
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

    fun onFavoriteClicked(track: Track) {
        track.isFavorite = track.isFavorite.not()
        playerStateLiveData.value?.let { currentState ->
            val newFavoriteState = !currentState.isFavorite
            viewModelScope.launch {
                if (newFavoriteState) {
                    favoritesInteractor.addTrackToFavorites(track)
                } else {
                    favoritesInteractor.deleteTrackFromFavorites(track)
                }
            }

            val newState = when (currentState) {
                is PlayerState.Default -> PlayerState.Default(newFavoriteState)
                is PlayerState.Prepared -> PlayerState.Prepared(newFavoriteState)
                is PlayerState.Playing -> PlayerState.Playing(currentState.progress, newFavoriteState)
                is PlayerState.Paused -> PlayerState.Paused(currentState.progress, newFavoriteState)
            }

            playerStateLiveData.postValue(newState)
        }
    }

    fun preparePlayer(url: String, trackId: Int){
        var isFavorite = false
        viewModelScope.launch {
            favoritesInteractor.getFavoritesTracksIds().collect {
                isFavorite = trackId in it
            }
        }
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared(isFavorite))
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState.Prepared(playerStateLiveData.value?.isFavorite ?: false))
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition(), playerStateLiveData.value?.isFavorite ?: false))
        startTimer()
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition(), playerStateLiveData.value?.isFavorite ?: false))
    }

    private fun releasePlayer(){
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default(playerStateLiveData.value?.isFavorite ?: false)
    }

    private fun startTimer(){
        timerJob = viewModelScope.launch{
            while (mediaPlayer.isPlaying){
                delay(300L)
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition(), playerStateLiveData.value?.isFavorite ?: false))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }




}