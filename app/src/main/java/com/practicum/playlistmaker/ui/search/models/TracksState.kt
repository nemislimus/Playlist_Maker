package com.practicum.playlistmaker.ui.search.models

import com.practicum.playlistmaker.domain.search.models.Track

sealed interface TracksState {

    data object Loading: TracksState

    data class  Content(
        val tracks: List<Track>
    ): TracksState

    data class  History(
        val historyTracks: List<Track>
    ): TracksState

    data class Error(
        val errorMessage: String
    ):TracksState

    data class Empty(
        val emptyMessage: String
    ):TracksState
}