package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.player.model.PlayerState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class PlayerViewModel: ViewModel() {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel()
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val mediaPlayer = MediaPlayer()
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    private val timerValue = MutableLiveData<Int>()

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
    fun observeTimerValue(): LiveData<Int> = timerValue


    private val updateTimeRunnable = object: Runnable{
        override fun run() {
            if (playerStateLiveData.value is PlayerState.Playing){
                val currentPos = mediaPlayer.currentPosition
                timerValue.postValue(currentPos)
                handler.postDelayed(this, 1000)
            }
        }
    }
    fun preparePlayer(url: String){
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTimeRunnable)
            playerStateLiveData.postValue(PlayerState.Prepared)
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing)
        handler.post(updateTimeRunnable)
    }

    fun pausePlayer(){
        mediaPlayer.pause()
        handler.removeCallbacks(updateTimeRunnable)
        playerStateLiveData.postValue(PlayerState.Paused)
    }


    fun playbackControl(){
        when(playerStateLiveData.value){
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
            else -> return
        }
    }

    fun releasePlayer(){
        mediaPlayer.release()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }
}