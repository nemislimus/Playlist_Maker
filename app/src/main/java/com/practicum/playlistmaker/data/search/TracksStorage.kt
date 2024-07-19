package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.search.models.TrackDto

interface TracksStorage {

    fun saveHistory(historyList: List<TrackDto>)

    fun getHistory(): ArrayList<TrackDto>?

    fun cleanHistory()
}