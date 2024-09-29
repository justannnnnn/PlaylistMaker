package com.example.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.ThemeInteractor

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        val themeInteractor = Creator.provideThemeInteractor()
        themeInteractor.applyTheme(object: ThemeInteractor.ThemeConsumer{
            override fun consume(isDark: Boolean) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        })
    }
}