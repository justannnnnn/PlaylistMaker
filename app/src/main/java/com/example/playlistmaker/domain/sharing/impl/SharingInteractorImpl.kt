package com.example.playlistmaker.domain.sharing.impl

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.dto.EmailDataDto
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val application: Application
) : SharingInteractor {

    override fun shareApp(): Intent {
        return externalNavigator.shareText(getShareAppLink())
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>): Intent {
        return externalNavigator.shareText(getPlaylistShareString(playlist, tracks))
    }

    override fun openTerms(): Intent {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport(): Intent {
        val temp = getSupportEmailData()
        return externalNavigator.openEmail(EmailDataDto(temp.rcpt, temp.subject, temp.text))
    }

    private fun getShareAppLink(): String = application.getString(R.string.share_message)

    private fun getPlaylistShareString(playlist: Playlist, tracks: List<Track>): String {
        return "${playlist.playlistName}\n" +
                "${playlist.playlistDescription}\n" +
                "${
                    application.resources.getQuantityString(
                        R.plurals.tracks_plurals,
                        playlist.countTracks,
                        playlist.countTracks
                    )
                }\n" +
                tracks.joinToString { track ->
                    "${tracks.indexOf(track) + 1}.${track.artistName} - " +
                            "${track.trackName}(${track.trackTime})\n"
                }
    }

    private fun getSupportEmailData(): EmailData = EmailData(
        application.getString(R.string.support_mail),
        application.getString(R.string.support_mail_subject),
        application.getString(R.string.support_mail_message)
    )

    private fun getTermsLink(): String = application.getString(R.string.agreement_uri)
}