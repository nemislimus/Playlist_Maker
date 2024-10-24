package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.PlaylistPoolTrackEntity

@Dao
interface PlaylistTracksDao {

    @Insert(entity = PlaylistPoolTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrackToPlaylistPool(trackEntity: PlaylistPoolTrackEntity)

    @Query("SELECT * FROM playlist_pool_tracks_table")
    suspend fun getAllTracksFromPlaylistPool(): List<PlaylistPoolTrackEntity>

    @Delete(entity = PlaylistPoolTrackEntity::class)
    suspend fun deleteTrack(trackEntity: PlaylistPoolTrackEntity)

}