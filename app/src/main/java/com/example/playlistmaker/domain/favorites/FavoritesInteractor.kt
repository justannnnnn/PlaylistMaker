package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun addTrackToFavorites(track: Track)

    fun deleteTrackFromFavorites(track: Track)

    fun getFavoritesTracks(): Flow<List<Track>>

    fun getFavoritesTracksIds(): Flow<List<Int>>
}