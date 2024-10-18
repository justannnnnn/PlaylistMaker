package com.example.playlistmaker.domain.search.impl


import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {


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