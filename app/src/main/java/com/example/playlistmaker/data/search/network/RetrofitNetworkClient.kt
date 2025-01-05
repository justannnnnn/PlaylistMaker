package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.Response
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val itunesService: ITunesAPIService
): NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return withContext(Dispatchers.IO){
                try {
                    val response = itunesService.searchSongs(dto.expr)
                    response.apply { resultCode = 200 }
                } catch (e: Exception) {
                    TrackSearchResponse("", dto.expr, ArrayList()).apply {
                        resultCode = -1
                    }
                }
            }
        } else {
            return TrackSearchResponse("", dto.toString(), ArrayList()).apply {
                resultCode = 400
            }
        }
    }
}