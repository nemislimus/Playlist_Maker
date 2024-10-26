package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlaylistInsideFragmentViewModel(
    private val playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor,
): ViewModel() {

    private val playlist = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlist

    private val _trackList = MutableStateFlow<List<Track>>(emptyList())
    fun observePlaylistTrackList() = _trackList.asStateFlow()

    fun sharePlaylist(playlistInfo: String) {
        sharingInteractor.sharePlaylist(playlistInfo)
    }

    suspend fun getCurrentPlaylistFromDb() {
        postPlaylistData(playlistsInteractor.getPlaylistById(playlistId))
    }

    suspend fun deleteTrack(track: Track, playlist: Playlist) {
        playlistsInteractor.deleteTrackFromPlaylist(track, playlist)
    }

    suspend fun deletePlaylist(playlist: Playlist) =
        playlistsInteractor.deletePlaylist(playlist)

    suspend fun getPlaylistTrackList(trackIds: List<Long>) {
        playlistsInteractor.getPlaylistTracksFromPool(trackIds).collect {
                tracks -> manageTrackListData(tracks)
        }
    }

    private fun postPlaylistData(data: Playlist?) {
        data?.let { playlist.postValue(it) }
    }

    private fun manageTrackListData(tracks: List<Track>) {
        _trackList.update { tracks }
    }
}