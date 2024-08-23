package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    override fun onCreate(){
        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object{
        const val PLAYLIST_PREFERENCES = "playlist_prefs" // name for sharedPrefs
        const val IS_DARK_THEME = "IS_DARK_THEME" // is dark theme enabled
        // search history
        const val LIST_KEY = "LIST_KEY" // search history list
        const val NEW_VAL_KEY = "NEW_VAL_KEY" // new track into search history list
    }
}