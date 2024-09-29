package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private const val PLAYLIST_PREFERENCES = "playlist_prefs" // name for sharedPrefs
    private const val HISTORY_TRACKLIST = "HISTORY_TRACKS"

    private lateinit var applicationContext: Context
    fun init(context: Context){
        applicationContext = context.applicationContext
    }
    private fun provideTrackRepository(): TrackRepository{
        val networkClient = RetrofitNetworkClient()
        return TrackRepositoryImpl(networkClient)
    }

    fun provideTrackInteractor(): TrackInteractor{
        val repository = provideTrackRepository()
        return TrackInteractorImpl(repository)
    }
    private fun provideThemeRepository(): ThemeRepository = ThemeRepositoryImpl(provideSharedPrefs(
        PLAYLIST_PREFERENCES))

    fun provideThemeInteractor(): ThemeInteractor{
        val repository = provideThemeRepository()
        return ThemeInteractorImpl(repository)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository = SearchHistoryRepositoryImpl(provideSharedPrefs(
        HISTORY_TRACKLIST))

    fun provideHistoryInteractor() : SearchHistoryInteractor{
        val repository = provideSearchHistoryRepository()
        return SearchHistoryInteractorImpl(repository)
    }

    private fun provideSharedPrefs(key: String): SharedPreferences = applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)
}