package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expr: String): Flow<ArrayList<Track>?> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expr))
        when (response.resultCode){
            -1 -> emit(null)
            200 -> {
                with (response as TrackSearchResponse) {
                    emit(ArrayList(response.results.mapNotNull {
                        try {
                            Track(
                                it.trackId,
                                it.trackName,
                                it.artistName,
                                SimpleDateFormat("mm:ss", Locale.getDefault()).format((it.trackTime ?: "0").toLong()),
                                it.artworkUrl100,
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName,
                                it.country,
                                it.previewUrl
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }))
                }
            }
            else -> throw Exception("Unexpected response type: ${response::class.java.simpleName}")
        }
    }

}