package com.practicum.playlistmaker.ui.mediateka.models

import com.practicum.playlistmaker.domain.db.models.Playlist

sealed interface PlaylistsState {

    data object Loading: PlaylistsState

    data class  Content(
        val playlists: List<Playlist>
    ): PlaylistsState

    data object Empty: PlaylistsState
}