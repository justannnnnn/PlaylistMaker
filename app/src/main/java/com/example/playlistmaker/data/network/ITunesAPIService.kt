package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPIService {

    @GET("/search?entity=song")
    fun searchSongs(@Query("term") query: String): Call<TrackSearchResponse>

}