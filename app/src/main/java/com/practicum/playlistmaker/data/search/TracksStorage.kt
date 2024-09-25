package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.search.models.TrackDto

interface TracksStorage {

    suspend fun saveHistory(historyList: List<TrackDto>)

    suspend fun getHistory(): ArrayList<TrackDto>?

    suspend fun cleanHistory()
}