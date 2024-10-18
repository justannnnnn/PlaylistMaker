package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {
    private const val PLAYLIST_PREFERENCES = "playlist_prefs" // name for sharedPrefs
    private const val HISTORY_TRACKLIST = "HISTORY_TRACKS"

    private lateinit var applicationContext: Context
    fun init(context: Context){
        applicationContext = context.applicationContext
    }
    private fun provideTrackRepository(): TrackRepository {
        val networkClient = RetrofitNetworkClient()
        return TrackRepositoryImpl(networkClient)
    }

    fun provideTrackInteractor(): TrackInteractor {
        val repository = provideTrackRepository()
        return TrackInteractorImpl(repository)
    }
    private fun provideSettingsRepository(): SettingsRepository = SettingsRepositoryImpl(
        provideSharedPrefs(
        PLAYLIST_PREFERENCES
        )
    )

    fun provideSettingsInteractor(): SettingsInteractor {
        val repository = provideSettingsRepository()
        return SettingsInteractorImpl(repository)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository = SearchHistoryRepositoryImpl(
        provideSharedPrefs(
        HISTORY_TRACKLIST
        )
    )

    fun provideHistoryInteractor() : SearchHistoryInteractor {
        val repository = provideSearchHistoryRepository()
        return SearchHistoryInteractorImpl(repository)
    }


    private fun provideSharedPrefs(key: String): SharedPreferences = applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)

    private fun provideExternalNavigator() : ExternalNavigator{
        return ExternalNavigatorImpl()
    }

    fun provideSharingInteractor() : SharingInteractor{
        val navigator = provideExternalNavigator()
        return SharingInteractorImpl(navigator, applicationContext as Application)
    }

}