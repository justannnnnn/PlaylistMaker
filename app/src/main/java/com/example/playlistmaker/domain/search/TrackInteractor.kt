package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expr: String) : Flow<Pair<List<Track>?, String?>>

}