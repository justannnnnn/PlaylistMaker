package com.example.playlistmaker.domain.favorites.impl

import com.example.playlistmaker.data.favorites.FavoritesRepository
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
): FavoritesInteractor {
    override fun addTrackToFavorites(track: Track) {
        favoritesRepository.addTrackToFavorites(track)
    }

    override fun deleteTrackFromFavorites(track: Track) {
         favoritesRepository.deleteTrackFromFavorites(track)
    }

    override fun getFavoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoritesTracks().map { it.reversed() }
    }

    override fun getFavoritesTracksIds(): Flow<List<Int>> {
        return favoritesRepository.getFavoritesTracksIds()
    }
}