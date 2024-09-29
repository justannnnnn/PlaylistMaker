package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesAPIService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val response = itunesService.searchSongs(dto.expr).execute()
                return if (response.isSuccessful) {
                    val body = response.body()
                    body?.apply {
                        resultCode = response.code()
                    }
                        ?: TrackSearchResponse("", dto.expr, ArrayList()).apply {
                            resultCode = response.code()
                        }
                } else {
                    TrackSearchResponse("", dto.expr, ArrayList()).apply {
                        resultCode = response.code()
                    }
                }
            } catch (e: Exception) {
                return TrackSearchResponse("", dto.expr, ArrayList()).apply {
                    resultCode = -1
                }
            }
        } else {
            return TrackSearchResponse("", dto.toString(), ArrayList()).apply {
                resultCode = 400
            }
        }
    }
}