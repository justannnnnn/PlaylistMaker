package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistTracksEntity

@Dao
interface PlaylistTracksDao {

    @Insert(entity = PlaylistTracksEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(playlistTrack: PlaylistTracksEntity): Long

    @Delete
    suspend fun deleteTrack(playlistTrack: PlaylistTracksEntity): Int

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): List<PlaylistTracksEntity>
}