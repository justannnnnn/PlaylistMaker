package com.example.playlistmaker.ui.media.favorites.model

import com.example.playlistmaker.domain.search.model.Track

sealed interface FavoritesState{
    data object isEmpty: FavoritesState

    data class Content(val tracks: List<Track>): FavoritesState

}