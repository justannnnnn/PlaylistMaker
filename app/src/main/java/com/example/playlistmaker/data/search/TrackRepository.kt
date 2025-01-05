package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expr: String): Flow<ArrayList<Track>?>
}