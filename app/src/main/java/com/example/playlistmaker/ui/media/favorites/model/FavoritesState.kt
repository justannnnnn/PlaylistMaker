package com.example.playlistmaker.ui.media.favorites.model

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavoritesState {
    data object IsEmpty : FavoritesState

    data class Content(val tracks: List<Track>) : FavoritesState
}