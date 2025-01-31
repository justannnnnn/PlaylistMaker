package com.example.playlistmaker.data.favorites.impl

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.data.favorites.FavoritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
): FavoritesRepository {
    override fun addTrackToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(mapToTrackEntity(track))
    }

    override fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(mapToTrackEntity(track))
    }

    override fun getFavoritesTracks(): Flow<List<Track>> = flow{
        val favorites = appDatabase.trackDao().getTracks()
        emit(mapFromTrackEntity(favorites))
    }

    override fun getFavoritesTracksIds(): Flow<List<Int>> = flow{
        val favoritesIds = appDatabase.trackDao().getTracksIds()
        emit(favoritesIds)
    }


    private fun mapFromTrackEntity(tracks: List<TrackEntity>): List<Track>{
        return tracks.map { trackDbConvertor.map(it) }
    }

    private fun mapToTrackEntity(track: Track): TrackEntity{
        return trackDbConvertor.map(track)
    }
}