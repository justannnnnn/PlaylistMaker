package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expr: String): ArrayList<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expr))
        if (response is TrackSearchResponse) {
            if (response.resultCode == -1) {
                throw Exception("Network error")
            }
            if (response.results.isEmpty()) {
                return ArrayList()
            }
            return ArrayList(response.results.mapNotNull {
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
            })
        } else {
            throw Exception("Unexpected response type: ${response::class.java.simpleName}")
        }
    }

}