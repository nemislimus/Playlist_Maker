package com.practicum.playlistmaker.domain.search.api

import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    suspend fun saveHistory(historyList: List<Track>)

    suspend fun getHistory(): ArrayList<Track>?

    suspend fun cleanHistory()
}