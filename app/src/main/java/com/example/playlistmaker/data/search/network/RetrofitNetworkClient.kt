package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.Response
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse

class RetrofitNetworkClient(
    private val itunesService: ITunesAPIService
): NetworkClient {

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