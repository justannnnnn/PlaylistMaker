package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(private val prefs: SharedPreferences): SearchHistoryRepository {

    private val historyTracks = ArrayList<Track>()
    private val gson = Gson()

    init{
        val str = prefs.getString(LIST_KEY, "")
        if (!str.isNullOrEmpty()) historyTracks.addAll(gson.fromJson(str, Array<Track>::class.java).toList())
    }

    override fun saveSearchHistory(track: Track) {
        if (track in historyTracks) historyTracks.remove(track)
        else if (historyTracks.size == 10) {
            historyTracks.removeLast()
        }
        historyTracks.add(0, track)
        prefs.edit().putString(LIST_KEY, gson.toJson(historyTracks)).apply()
    }

    override fun getSearchHistory(): List<Track> = historyTracks

    override fun clearSearchHistory() {
        historyTracks.clear()
        prefs.edit().remove(LIST_KEY).apply()
    }



        private companion object {
        // search history
        const val LIST_KEY = "LIST_KEY" // search history list
    }


}
