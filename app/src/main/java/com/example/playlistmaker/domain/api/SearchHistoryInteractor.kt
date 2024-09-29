package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: SearchHistoryConsumer)

    fun saveHistory(track: Track)

    fun clearSearchHistory()

    interface SearchHistoryConsumer{
        fun consume(historyTracks: ArrayList<Track>)
        fun onError(errorMessage: String)
    }
}