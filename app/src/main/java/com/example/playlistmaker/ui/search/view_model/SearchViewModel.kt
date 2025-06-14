package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.model.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyInteractor: SearchHistoryInteractor,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private val searchStateLiveData = MutableLiveData<TracksState>()
    private val historyChangedLiveData = MutableLiveData<Boolean>()


    fun observeSearchState(): LiveData<TracksState> = searchStateLiveData
    fun observeHistoryChanged(): LiveData<Boolean> = historyChangedLiveData


    fun searchDebounce(changedText: String) {
        if (changedText == "") searchJob?.cancel()
        if (latestSearchText == changedText &&
            searchStateLiveData.value !is TracksState.Error
        ) return

        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }


    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)


            viewModelScope.launch {
                trackInteractor.searchTracks(newSearchText)
                    .collect { pair ->
                        val tracks = pair.first
                        if (tracks == null) renderState(TracksState.Error(pair.second!!))
                        else {
                            if (tracks.isEmpty()) renderState(TracksState.Empty)
                            else renderState(TracksState.Content(ArrayList(tracks)))
                        }
                    }
            }
        }
    }

    private fun renderState(state: TracksState) {
        searchStateLiveData.postValue(state)
    }

    fun clearHistory() {
        historyInteractor.clearSearchHistory()
        historyChangedLiveData.postValue(false)
        historyChangedLiveData.postValue(true)
    }

    fun saveHistory(track: Track) {
        historyInteractor.saveHistory(track)
        historyChangedLiveData.postValue(false)
        historyChangedLiveData.postValue(true)
    }

    fun getHistoryTracks(): ArrayList<Track> {
        val history = ArrayList<Track>()
        historyInteractor.getHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(historyTracks: ArrayList<Track>) {
                history.addAll(historyTracks)
            }

            override fun onError(errorMessage: String) {
                return
            }

        })
        return history
    }

}