package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}