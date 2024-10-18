package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryRepository {
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}