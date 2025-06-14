package com.example.playlistmaker.domain.favorites.playlists

import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun addPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackInPlaylist(track: Track, playlist: Playlist)

    fun isTrackInPlaylist(trackId: Int, playlist: Playlist): Flow<Boolean>

    fun getTrackById(trackId: Int): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}