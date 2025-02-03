package com.example.playlistmaker.domain.favorites.playlists.model

import java.io.Serializable


data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    val tracks: List<Int> = emptyList(),
    val countTracks: Int = 0
) : Serializable
