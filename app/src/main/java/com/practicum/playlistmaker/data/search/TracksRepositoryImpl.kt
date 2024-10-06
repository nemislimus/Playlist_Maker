package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.search.models.TrackDto
import com.practicum.playlistmaker.data.search.models.TracksSearchResponse
import com.practicum.playlistmaker.data.search.models.TracksSearchRequest
import com.practicum.playlistmaker.domain.search.api.TracksRepository
import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksStorage: TracksStorage,
    private val favoriteDao: FavoriteTrackDao,
): TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {

            NetworkClient.NO_CONNECTION_CODE -> {
                emit(Resource.Error(Resource.CHECK_CONNECTION))
            }

            NetworkClient.SUCCESS_CODE -> {
                val data = (response as TracksSearchResponse).results.map { trackDto ->
                    TrackDto.convertToTrack(trackDto)
                }.let { data -> updateFavoriteStatus(data) }

                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(Resource.SERVER_ERROR))
            }
        }
    }

    override suspend fun saveHistory(historyList: List<Track>) {
        val trackHistoryList = historyList.map { TrackDto.trackToTrackDto(it) }
        tracksStorage.saveHistory(trackHistoryList)
    }

    override suspend fun getHistory(): ArrayList<Track>? {
        val historyTracks = tracksStorage.getHistory()?.map { TrackDto.convertToTrack(it) }
        return historyTracks?.let { tracks -> updateFavoriteStatus(tracks) } as? ArrayList<Track>?
    }

    override suspend fun cleanHistory() {
        tracksStorage.cleanHistory()
    }

    private suspend fun updateFavoriteStatus(tracks: List<Track>): List<Track> {
        val favoritesId = favoriteDao.getAllFavoriteId().toSet()
        return tracks.map { track ->
            track.copy(isFavorite = favoritesId.contains(track.trackId))
        }
    }

}