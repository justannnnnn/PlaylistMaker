package com.example.playlistmaker.data.favorites.playlists.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTracksEntity
import com.example.playlistmaker.data.favorites.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val gson: Gson
) : PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(mapToPlaylistEntity(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(mapToPlaylistEntity(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        val result = mapFromPlaylistEntity(playlists)
        emit(result)
    }

    override fun getTracksInPlaylist(playlistId: Int): Flow<List<Int>> = flow {
        emit(
            gson.fromJson(
                appDatabase.playlistDao().getTracksInPlaylist(playlistId),
                Array<Int>::class.java
            ).toList()
        )
    }

    override suspend fun addTrackInPlaylist(track: Track, playlist: Playlist) {
        val tracksNew = playlist.tracks.toMutableList()
        tracksNew.add(track.trackId)
        addPlaylist(playlist.copy(tracks = tracksNew, countTracks = playlist.countTracks + 1))

        appDatabase.playlistTracksDao().insertTrack(mapToPlaylistTrackEntity(track))
    }

    override fun getPlaylistTrackById(trackId: Int): Flow<List<Int>> = flow{
        emit(appDatabase.playlistTracksDao().getTrackById(trackId).map { it.trackId })
    }

    private fun mapFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            Playlist(
                playlistId = it.playlistId,
                playlistName = it.playlistName,
                playlistDescription = it.playlistDescription,
                coverPath = it.coverPath,
                tracks = gson.fromJson(it.tracks, Array<Int>::class.java).toList(),
                countTracks = it.countTracks
            )
        }
    }

    private fun mapToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            coverPath = playlist.coverPath,
            tracks = gson.toJson(playlist.tracks),
            countTracks = playlist.countTracks
        )
    }

    private fun mapToPlaylistTrackEntity(track: Track) = PlaylistTracksEntity(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.collectionName,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl
    )
}