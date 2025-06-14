package com.example.playlistmaker.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.network.ITunesAPIService
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesAPIService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesAPIService::class.java)
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

}
