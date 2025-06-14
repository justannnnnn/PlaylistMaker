package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.settings.SettingsRepository

class SettingsRepositoryImpl(private val prefs: SharedPreferences) : SettingsRepository {
    override fun isDarkTheme(): Boolean {
        return prefs.getBoolean(IS_DARK_THEME, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(IS_DARK_THEME, isDark).apply()
    }

    private companion object {
        const val IS_DARK_THEME = "IS_DARK_THEME"
    }
}