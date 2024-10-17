package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorite(trackEntity: TrackEntity)

    @Query("DELETE FROM tracks_table WHERE track_id = :trackId")
    suspend fun removeFromFavorite(trackId: Long)

    @Query("SELECT * FROM tracks_table ORDER BY id DESC")
    suspend fun getAllFavoriteTracks(): List<TrackEntity>

    @Query("SELECT track_id FROM tracks_table")
    suspend fun getAllFavoriteId(): List<Long>

}