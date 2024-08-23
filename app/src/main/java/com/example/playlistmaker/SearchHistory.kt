package com.example.playlistmaker

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.gson.Gson

class SearchHistory(val data: SharedPreferences, val adapter: TrackAdapter) {

    // listener of adding new track in history
    private val listener = OnSharedPreferenceChangeListener{ sp, key ->
        if (key == App.NEW_VAL_KEY){
            val trackStr = sp.getString(App.NEW_VAL_KEY, null)
            val track = Gson().fromJson(trackStr, Track::class.java)
            if (track != null){
                if (track.trackId in tracks.map { it.trackId }){
                    val ind = tracks.indexOfFirst { it.trackId == track.trackId }
                    tracks.removeAt(ind)
                    adapter.notifyItemRemoved(ind)
                }
                /*if (tracks.size == 10) {
                    val ind = tracks.size - 1
                    tracks.removeAt(ind)
                    adapter.notifyItemRemoved(ind)
                }*/
                tracks.add(0, track)
                adapter.notifyItemInserted(0)
                sp.edit()
                    .putString(App.LIST_KEY, Gson().toJson(tracks))
                    .apply()
            }
        }
    }
    private val tracks = ArrayList<Track>()
    init{
        data.registerOnSharedPreferenceChangeListener(listener)
        val trackStr = data.getString(App.LIST_KEY,  "")
        if (trackStr != ""){
            val arrTracks = Gson().fromJson(trackStr, Array<Track>::class.java)
            tracks.addAll(arrTracks)
        }
    }

    fun getTracks() = tracks

    fun clearHistory(){
        tracks.clear()
        data.edit().clear().apply()
    }
}