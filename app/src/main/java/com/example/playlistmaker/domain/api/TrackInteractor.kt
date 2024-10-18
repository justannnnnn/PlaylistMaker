package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTracks(expr: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: ArrayList<Track>)
        fun onError(errorMessage: String)
    }
}