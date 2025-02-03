package com.example.playlistmaker.ui.media.playlists.new_playlist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun onCreateButtonClicked(playlist: Playlist){
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(playlist)
        }
    }

}