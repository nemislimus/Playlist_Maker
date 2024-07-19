package com.practicum.playlistmaker.domain.search.api

import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track

interface TracksRepository {

    fun searchTracks(expression: String): Resource<List<Track>>

    fun saveHistory(historyList: List<Track>)

    fun getHistory(): ArrayList<Track>?

    fun cleanHistory()
}