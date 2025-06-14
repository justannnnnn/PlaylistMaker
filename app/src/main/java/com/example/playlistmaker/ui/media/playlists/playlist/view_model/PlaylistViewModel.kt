package com.example.playlistmaker.ui.media.playlists.playlist.view_model

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val tracksLiveData = MutableLiveData<List<Track>>(emptyList())
    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observeTracksState(): LiveData<List<Track>> = tracksLiveData
    fun observePlaylistState(): LiveData<Playlist?> = playlistLiveData

    fun getTracks(tracksIds: List<Int>) {
        viewModelScope.launch {
            val tracks = mutableListOf<Track>()
            tracksIds.map { trackId ->
                playlistsInteractor.getTrackById(trackId)
                    .collect { trackList ->
                        val track = trackList.firstOrNull()
                        track?.let {
                            tracks.add(it)
                        }
                    }
            }
            tracksLiveData.postValue(tracks)
        }
    }

    fun updatePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { playlistList ->
                    playlistList.find { it.playlistId == playlistId }?.let {
                        playlistLiveData.postValue(it)
                    }
                }
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(track, playlist)
        }
        val tracks = tracksLiveData.value?.toMutableList()
        tracks?.apply {
            remove(track)
        }
        tracksLiveData.postValue(tracks ?: emptyList())
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            tracksLiveData.value?.forEach { track ->
                deleteTrackFromPlaylist(track, playlist)
            }
            playlistsInteractor.deletePlaylist(playlist)
        }
    }

    fun sharePlaylistTracks(playlist: Playlist): Intent {
        return sharingInteractor.sharePlaylist(
            playlist,
            tracksLiveData.value ?: emptyList()
        )
    }
}