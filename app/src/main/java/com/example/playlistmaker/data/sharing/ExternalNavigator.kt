package com.example.playlistmaker.data.sharing

import android.content.Intent
import com.example.playlistmaker.data.sharing.dto.EmailDataDto

interface ExternalNavigator {
    fun shareText(text: String): Intent
    fun openLink(link: String): Intent
    fun openEmail(email: EmailDataDto): Intent
}