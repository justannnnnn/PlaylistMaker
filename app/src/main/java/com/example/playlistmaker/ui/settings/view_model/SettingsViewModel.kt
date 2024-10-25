package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    init{
        this.setTheme(settingsInteractor.isDarkTheme())
    }

    companion object {
        fun getViewModelFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }

    fun getTheme() = settingsInteractor.isDarkTheme()

    fun setTheme(isDark: Boolean){
        settingsInteractor.setTheme(isDark)
        settingsInteractor.applyTheme(object: SettingsInteractor.ThemeConsumer{
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
    fun openSupport(): Intent{
        return sharingInteractor.openSupport()
    }

    fun openAgreement(): Intent{
        return sharingInteractor.openTerms()
    }


}