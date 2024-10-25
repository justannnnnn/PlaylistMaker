package com.example.playlistmaker.domain.settings

interface SettingsInteractor {
    fun isDarkTheme(): Boolean

    fun setTheme(darkThemeEnabled: Boolean)

    fun applyTheme(consumer: ThemeConsumer)

    interface ThemeConsumer{
        fun consume(isDark: Boolean)
    }
}