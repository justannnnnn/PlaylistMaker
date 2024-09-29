package com.example.playlistmaker.presentation.theme_manager

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.ThemeRepository

object ThemeManager {
    private val preferencesRepository: ThemeRepository = Creator.providePrefsRepository()

    fun isDarkTheme() : Boolean{
        return preferencesRepository.isDarkTheme()
    }
    fun applyTheme(){
        val isDarkTheme = isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        preferencesRepository.setDarkTheme(darkThemeEnabled)
        applyTheme()
    }
}
