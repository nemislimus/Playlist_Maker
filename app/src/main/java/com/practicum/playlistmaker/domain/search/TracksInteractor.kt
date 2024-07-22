package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.models.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun getTrackListHistory(): ArrayList<Track>?

    fun putTrackToHistoryList(track: Track)

    fun cleanTrackListHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, message: String?)
    }
}