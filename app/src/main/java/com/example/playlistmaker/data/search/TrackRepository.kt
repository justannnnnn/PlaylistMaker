package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.search.model.Track

interface TrackRepository {
    fun searchTracks(expr: String): ArrayList<Track>?
}