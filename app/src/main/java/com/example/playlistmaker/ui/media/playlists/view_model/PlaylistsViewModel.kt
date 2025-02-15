package com.example.playlistmaker.ui.media.playlists.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<List<Playlist>>(emptyList())
    fun observePlaylistsState(): LiveData<List<Playlist>> = playlistsLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect {
                    playlistsLiveData.postValue(it)
                }
        }
    }

}