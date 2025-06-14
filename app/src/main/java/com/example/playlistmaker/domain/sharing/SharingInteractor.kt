package com.example.playlistmaker.domain.sharing

import android.content.Intent
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

interface SharingInteractor {
    fun shareApp(): Intent
    fun sharePlaylist(playlist: Playlist, tracks: List<Track>): Intent
    fun openTerms(): Intent
    fun openSupport(): Intent
}