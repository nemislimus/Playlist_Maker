package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.models.Playlist

class UpdatePlaylistFragmentViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Long,
): NewPlaylistFragmentViewModel(playlistsInteractor) {

    private val playlist = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlist

    suspend fun getCurrentPlaylistFromDb() {
        postPlaylistData(playlistsInteractor.getPlaylistById(playlistId))
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistsInteractor.updatePlaylist(playlist)
    }

    suspend fun deleteCoverFromPrivateStorage(coverPath: String) =
        playlistsInteractor.deleteCoverFromPrivateStorage(coverPath)

    private fun postPlaylistData(data: Playlist?) {
        data?.let { playlist.postValue(it) }
    }

}