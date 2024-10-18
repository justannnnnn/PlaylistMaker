package com.example.playlistmaker.data.settings

interface SettingsRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}