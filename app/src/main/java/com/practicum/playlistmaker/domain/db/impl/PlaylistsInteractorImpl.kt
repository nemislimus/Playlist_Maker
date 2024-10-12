package com.practicum.playlistmaker.domain.db.impl

import android.content.Context
import android.net.Uri
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.api.PlaylistsRepository
import com.practicum.playlistmaker.domain.db.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
): PlaylistsInteractor {

    override suspend fun savePlaylist(playlist: Playlist) {
       repository.savePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getAllPlaylists()

    override suspend fun getPlaylistByName(playlistName: String): Playlist? =
        repository.getPlaylistByName(playlistName)

    override suspend fun addTrackToPlaylistByName(playlistName: String, trackId: Long): Int =
        repository.addTrackToPlaylistByName(playlistName, trackId)

    override suspend fun saveCoverToPrivateStorage(
        context: Context,
        uri: Uri,
        coverIndex: Int
    ): String {
        return repository.saveCoverToPrivateStorage(context,uri,coverIndex)
    }
}