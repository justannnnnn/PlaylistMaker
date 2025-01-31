package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.favorites.FavoritesRepository
import com.example.playlistmaker.data.favorites.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module{
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(androidContext()
            .getSharedPreferences("HISTORY_TRACKS", Context.MODE_PRIVATE), get())
    }

    single<TrackRepository>{
        TrackRepositoryImpl(get(), get())
    }

    single<SettingsRepository>{
        SettingsRepositoryImpl(androidContext()
            .getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE))
    }


    factory<TrackDbConvertor> {
        TrackDbConvertor()
    }

    single<FavoritesRepository>{
        FavoritesRepositoryImpl(get(), get())
    }

}