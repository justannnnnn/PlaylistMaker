package com.example.playlistmaker.ui.settings.view_model

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    init {
        this.setTheme(settingsInteractor.isDarkTheme())
    }

    fun getTheme() = settingsInteractor.isDarkTheme()

    fun setTheme(isDark: Boolean) {
        settingsInteractor.setTheme(isDark)
        settingsInteractor.applyTheme(object : SettingsInteractor.ThemeConsumer {
            override fun consume(isDark: Boolean) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        })
    }

    fun shareApp(): Intent {
        return sharingInteractor.shareApp()
    }

    fun openSupport(): Intent {
        return sharingInteractor.openSupport()
    }

    fun openAgreement(): Intent {
        return sharingInteractor.openTerms()
    }
}