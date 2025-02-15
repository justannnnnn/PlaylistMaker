package com.example.playlistmaker.ui.media.playlists.new_playlist.view_model

import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor

class EditPlaylistViewModel(
    playlistsInteractor: PlaylistsInteractor
) : NewPlaylistViewModel(playlistsInteractor)