package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    private val playlistsLiveData = MutableLiveData<List<Playlist>>(emptyList())
    private val wasTrackAddedToPlaylist = MutableLiveData<Boolean?>(null)

    private var timerJob: Job? = null

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
    fun observePlaylistsState(): LiveData<List<Playlist>> = playlistsLiveData
    fun observeAddingInPlaylistState(): LiveData<Boolean?> = wasTrackAddedToPlaylist

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onAddButtonClicked() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect {
                    playlistsLiveData.postValue(it)
                }
        }
    }

    fun onPlaylistClicked(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            playlistsInteractor.isTrackInPlaylist(track.trackId, playlist)
                .take(1)
                .collect {
                    if (it)
                        wasTrackAddedToPlaylist.postValue(false)
                    else {
                        playlistsInteractor.addTrackInPlaylist(track, playlist)
                        wasTrackAddedToPlaylist.postValue(true)
                    }
                }
            playlistsInteractor.getPlaylists()
                .collect {
                    playlistsLiveData.postValue(it)
                }
            wasTrackAddedToPlaylist.postValue(null)
        }
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
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
                is PlayerState.Playing -> PlayerState.Playing(
                    currentState.progress,
                    newFavoriteState
                )

                is PlayerState.Paused -> PlayerState.Paused(
                    currentState.progress,
                    newFavoriteState
                )
            }

            playerStateLiveData.postValue(newState)
        }
    }

    fun preparePlayer(url: String, trackId: Int) {
        var isFavorite = false
        viewModelScope.launch {
            favoritesInteractor.getFavoritesTracksIds().collect {
                isFavorite = trackId in it
            }
        }
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerStateLiveData.postValue(PlayerState.Prepared(isFavorite))
            }
            mediaPlayer.setOnCompletionListener {
                timerJob?.cancel()
                playerStateLiveData.postValue(
                    PlayerState.Prepared(
                        playerStateLiveData.value?.isFavorite ?: false
                    )
                )
            }
        } catch (_: IllegalStateException) { }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(
            PlayerState.Playing(
                getCurrentPlayerPosition(),
                playerStateLiveData.value?.isFavorite ?: false
            )
        )
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(
            PlayerState.Paused(
                getCurrentPlayerPosition(),
                playerStateLiveData.value?.isFavorite ?: false
            )
        )
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value =
            PlayerState.Default(playerStateLiveData.value?.isFavorite ?: false)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playerStateLiveData.postValue(
                    PlayerState.Playing(
                        getCurrentPlayerPosition(),
                        playerStateLiveData.value?.isFavorite ?: false
                    )
                )
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition) ?: "00:00"
    }


}