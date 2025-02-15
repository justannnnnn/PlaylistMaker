package com.example.playlistmaker.data.favorites.playlists

import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getTracksInPlaylist(playlistId: Int): Flow<List<Int>>

    suspend fun addTrackInPlaylist(track: Track, playlist: Playlist)

    fun getPlaylistTrackById(trackId: Int): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(track: Track)

}