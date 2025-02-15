package com.example.playlistmaker.ui.media.playlists.new_playlist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    open fun onCreateButtonClicked(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(playlist)
        }
    }

}