package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun isDarkTheme(): Boolean

    fun setTheme(darkThemeEnabled: Boolean)

    fun applyTheme(consumer: ThemeConsumer)

    interface ThemeConsumer{
        fun consume(isDark: Boolean)
    }
}