package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String) : Flow<Pair<List<Track>?, String?>>

    suspend fun getTrackListHistory(): ArrayList<Track>?

    suspend fun putTrackToHistoryList(track: Track)

    suspend fun cleanTrackListHistory()

}