package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    val tracks: String,
    val countTracks: Int
)