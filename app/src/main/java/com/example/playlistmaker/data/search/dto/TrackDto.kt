package com.example.playlistmaker.data.search.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String?,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
):Serializable