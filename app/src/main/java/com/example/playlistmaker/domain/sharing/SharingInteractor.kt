package com.example.playlistmaker.domain.sharing

import android.content.Intent

interface SharingInteractor {
    fun shareApp(): Intent
    fun openTerms(): Intent
    fun openSupport(): Intent
}