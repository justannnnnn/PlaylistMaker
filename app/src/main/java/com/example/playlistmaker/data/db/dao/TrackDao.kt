package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Delete
    suspend fun deleteTrack(track: TrackEntity): Int

    @Query("SELECT * FROM favorites_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites_table")
    suspend fun getTracksIds(): List<Int>

}