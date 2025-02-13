package com.example.playlistmaker.di

import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.impl.FavoritesInteractorImpl
import com.example.playlistmaker.domain.favorites.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.favorites.playlists.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val interactorModule = module{

    single<SearchHistoryInteractor>{
        SearchHistoryInteractorImpl(get())
    }

    single<TrackInteractor>{
        TrackInteractorImpl(get())
    }

    single<SettingsInteractor>{
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor>{
        SharingInteractorImpl(get(), androidApplication())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistsInteractor>{
        PlaylistsInteractorImpl(get())
    }
}