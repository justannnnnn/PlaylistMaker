package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {

    fun getHistory(consumer: SearchHistoryConsumer)

    fun saveHistory(track: Track)

    fun clearSearchHistory()

    interface SearchHistoryConsumer{
        fun consume(historyTracks: ArrayList<Track>)
        fun onError(errorMessage: String)
    }
}