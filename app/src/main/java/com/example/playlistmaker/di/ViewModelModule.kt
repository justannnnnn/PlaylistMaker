package com.example.playlistmaker.di

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.ui.media.favorites.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.media.playlists.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{

    factory{
        Handler(Looper.getMainLooper())
    }

    factory{
        MediaPlayer()
    }

    viewModel{
        PlayerViewModel(get(), get())
    }

    viewModel{
        SearchViewModel(get(), get(), androidApplication())
    }

    viewModel{
        SettingsViewModel(get(), get())
    }

    viewModel{
        FavoritesViewModel(get())
    }

    viewModel{
        PlaylistsViewModel()
    }
}