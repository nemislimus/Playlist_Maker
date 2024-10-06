package com.practicum.playlistmaker.domain.db.api

import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addToFavorite(track: Track)

    suspend fun removeFromFavorite(track: Track)

    fun getAllFavoriteTracks(): Flow<List<Track>>

    suspend fun getAllFavoriteId(): List<Long>
}