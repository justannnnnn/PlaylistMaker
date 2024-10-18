package com.example.playlistmaker.ui.search.model

import com.example.playlistmaker.domain.search.model.Track

sealed interface TracksState {
    data object Loading: TracksState

    data class Content(
        val tracks: ArrayList<Track>
    ): TracksState

    data class Error(
        val errorCode: String
    ): TracksState

    data object Empty: TracksState
}
