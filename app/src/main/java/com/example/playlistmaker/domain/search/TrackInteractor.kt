package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TrackInteractor {
    fun searchTracks(expr: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: ArrayList<Track>)
        fun onError(errorMessage: String)
    }
}