package com.practicum.playlistmaker.ui.mediateka.view_models

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.models.Playlist

class NewPlaylistFragmentViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {

    suspend fun savePlaylist(playlist: Playlist) {
        playlistsInteractor.savePlaylist(playlist)
    }

    suspend fun saveCoverToPrivateStorage(uri: Uri, coverIndex: Int): String {
        return playlistsInteractor.saveCoverToPrivateStorage(
            uri,
            coverIndex
        )
    }
}