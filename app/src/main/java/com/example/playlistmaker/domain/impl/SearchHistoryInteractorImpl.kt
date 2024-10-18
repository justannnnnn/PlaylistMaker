package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {


    override fun getHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        val tracks = repository.getSearchHistory()
        consumer.consume(ArrayList(tracks))

    }

    override fun saveHistory(track: Track) {
        repository.saveSearchHistory(track)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }


}