package com.practicum.playlistmaker.data.favorite

import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val favoriteDao: FavoriteTrackDao
): FavoriteTracksRepository {

    override suspend fun addToFavorite(track: Track) {
        favoriteDao.addToFavorite(TrackEntity.trackToEntity(track))
    }

    override suspend fun removeFromFavorite(track: Track) {
        favoriteDao.removeFromFavorite(TrackEntity.trackToEntity(track).trackId)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        favoriteDao.getAllFavoriteTracks().map { entity ->
            TrackEntity.entityToTrack(entity)
        }
    }

    override suspend fun getAllFavoriteId(): List<Long> {
        return favoriteDao.getAllFavoriteId()
    }

}