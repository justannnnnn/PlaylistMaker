package com.example.playlistmaker.domain.sharing.impl

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.dto.EmailDataDto
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val application: Application
) : SharingInteractor {

    override fun shareApp() : Intent {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() : Intent {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() : Intent{
        val temp = getSupportEmailData()
        return externalNavigator.openEmail(EmailDataDto(temp.rcpt, temp.subject, temp.text))
    }

    private fun getShareAppLink() : String = application.getString(R.string.share_message)

    private fun getSupportEmailData(): EmailData = EmailData(application.getString(R.string.support_mail),
                                                            application.getString(R.string.support_mail_subject),
                                                            application.getString(R.string.support_mail_message))

    private fun getTermsLink(): String = application.getString(R.string.agreement_uri)
}