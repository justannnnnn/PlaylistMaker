package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.SettingsInteractor

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        val settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.applyTheme(object: SettingsInteractor.ThemeConsumer{
            override fun consume(isDark: Boolean) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        })
    }
}