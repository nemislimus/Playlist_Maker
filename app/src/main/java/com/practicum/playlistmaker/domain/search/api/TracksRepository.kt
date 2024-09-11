package com.practicum.playlistmaker.domain.search.api

import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun saveHistory(historyList: List<Track>)

    fun getHistory(): ArrayList<Track>?

    fun cleanHistory()
}