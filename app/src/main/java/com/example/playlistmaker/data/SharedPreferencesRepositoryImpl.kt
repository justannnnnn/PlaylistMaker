package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class SharedPreferencesRepositoryImpl(private val sp: SharedPreferences): ThemeRepository {
    override fun isDarkTheme(): Boolean {
        return sp.getBoolean(IS_DARK_THEME, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sp.edit().putBoolean(IS_DARK_THEME, isDark).apply()
    }

    private companion object{
        const val IS_DARK_THEME = "IS_DARK_THEME"
    }


}