package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)

    fun getFavoritesTracks(): Flow<List<Track>>

    fun getFavoritesTracksIds(): Flow<List<Int>>
}