package com.example.playlistmaker.presentation.theme_manager

import android.app.Application
import com.example.playlistmaker.Creator

class SwitchTheme: Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        ThemeManager.applyTheme()
    }
}