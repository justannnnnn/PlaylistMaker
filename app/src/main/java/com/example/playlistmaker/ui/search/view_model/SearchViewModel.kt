package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.model.TracksState

class SearchViewModel(private val trackInteractor: TrackInteractor, private val historyInteractor: SearchHistoryInteractor, private val handler: Handler, application: Application): AndroidViewModel(application) {

    companion object{
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private var latestSearchText: String? = null

    private val searchStateLiveData = MutableLiveData<TracksState>()
    private val historyChangedLiveData = MutableLiveData<Boolean>()


    fun observeSearchState(): LiveData<TracksState> = searchStateLiveData
    fun observeHistoryChanged(): LiveData<Boolean> = historyChangedLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String){
        if (changedText == "") handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (latestSearchText == changedText && searchStateLiveData.value !is TracksState.Error) return

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { search(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }


    private fun search(newSearchText: String){
        if (newSearchText.isNotEmpty()){
            renderState(TracksState.Loading)

            trackInteractor.searchTracks(newSearchText, object: TrackInteractor.TrackConsumer{
                override fun consume(foundTracks: ArrayList<Track>) {
                    val tracks = ArrayList<Track>()
                    if (foundTracks.isNotEmpty()) {
                        tracks.addAll(foundTracks)
                        renderState(TracksState.Content(tracks))
                    }
                    else renderState(TracksState.Empty)
                }

                override fun onError(errorMessage: String) {
                    renderState(TracksState.Error(errorMessage))
                }

            })
        }
    }

    private fun renderState(state: TracksState){
        searchStateLiveData.postValue(state)
    }

    fun clearHistory(){
        historyInteractor.clearSearchHistory()
        historyChangedLiveData.postValue(false)
        historyChangedLiveData.postValue(true)
    }

    fun saveHistory(track: Track){
        historyInteractor.saveHistory(track)
        historyChangedLiveData.postValue(false)
        historyChangedLiveData.postValue(true)
    }

    fun getHistoryTracks(): ArrayList<Track>{
        val history = ArrayList<Track>()
        historyInteractor.getHistory(object : SearchHistoryInteractor.SearchHistoryConsumer{
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