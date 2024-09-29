package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun isDarkTheme(): Boolean {
        return repository.isDarkTheme()
    }

    override fun setTheme(darkThemeEnabled: Boolean) {
        repository.setDarkTheme(darkThemeEnabled)
    }

    override fun applyTheme(consumer: ThemeInteractor.ThemeConsumer) {
        consumer.consume(repository.isDarkTheme())
    }

}