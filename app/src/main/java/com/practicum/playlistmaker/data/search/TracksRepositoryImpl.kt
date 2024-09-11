package com.practicum.playlistmaker.data.search

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
): TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {

            NetworkClient.NO_CONNECTION_CODE -> {
                emit(Resource.Error(Resource.CHECK_CONNECTION))
            }

            NetworkClient.SUCCESS_CODE -> {
                val data = (response as TracksSearchResponse).results.map {
                    TrackDto.convertToTrack(it)
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(Resource.SERVER_ERROR))
            }
        }
    }

    override fun saveHistory(historyList: List<Track>) {
        val trackHistoryList = historyList.map { TrackDto.trackToTrackDto(it) }
        tracksStorage.saveHistory(trackHistoryList)
    }

    override fun getHistory(): ArrayList<Track>? {
        return tracksStorage.getHistory()?.map { TrackDto.convertToTrack(it) } as ArrayList<Track>?
    }

    override fun cleanHistory() {
        tracksStorage.cleanHistory()
    }

}