package com.example.playlistmaker.domain.favorites.playlists.impl

import com.example.playlistmaker.data.favorites.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
) : PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackInPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
        playlistsRepository.addTrackInPlaylist(track, playlist)
    }

    override fun isTrackInPlaylist(trackId: Int, playlist: Playlist): Flow<Boolean> {
        return combine(
            playlistsRepository.getPlaylistTrackById(trackId),
            playlistsRepository.getTracksInPlaylist(playlist.playlistId)
        ) { playlistTracksById, tracksInPlaylist ->
            playlistTracksById.isNotEmpty() && tracksInPlaylist.contains(trackId)
        }
    }

    override fun getTrackById(trackId: Int): Flow<List<Track>> {
        return playlistsRepository.getPlaylistTrackById(trackId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.deleteTrackFromPlaylist(track)
        val countTracks = playlist.countTracks - 1
        val tracks = playlist.tracks.toMutableList()
        tracks.remove(track.trackId)
        playlistsRepository.addPlaylist(playlist.copy(countTracks = countTracks, tracks = tracks))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }
}