package com.practicum.playlistmaker.domain.db.impl

import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
): FavoriteTracksInteractor {

    override suspend fun addToFavorite(track: Track) {
        repository.addToFavorite(track)
    }

    override suspend fun removeFromFavorite(track: Track) {
        repository.removeFromFavorite(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return repository.getAllFavoriteTracks()
    }

    override suspend fun getAllFavoriteId(): List<Long> {
        return repository.getAllFavoriteId()
    }
}