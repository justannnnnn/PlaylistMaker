package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkTheme(): Boolean {
        return repository.isDarkTheme()
    }

    override fun setTheme(darkThemeEnabled: Boolean) {
        repository.setDarkTheme(darkThemeEnabled)
    }

    override fun applyTheme(consumer: SettingsInteractor.ThemeConsumer) {
        consumer.consume(repository.isDarkTheme())
    }
}