package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val prefs: SharedPreferences): ThemeRepository {
    override fun isDarkTheme(): Boolean {
        return prefs.getBoolean(IS_DARK_THEME, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(IS_DARK_THEME, isDark).apply()
    }

    private companion object{
        const val IS_DARK_THEME = "IS_DARK_THEME"
    }


}