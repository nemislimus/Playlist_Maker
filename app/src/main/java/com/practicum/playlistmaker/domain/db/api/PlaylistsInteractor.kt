package com.practicum.playlistmaker.domain.db.api

import android.net.Uri
import com.practicum.playlistmaker.domain.db.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun saveCoverToPrivateStorage(uri: Uri, coverIndex: Int): String

    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistByName(playlistName: String): Playlist?

    suspend fun addTrackIdToPlaylistByName(playlistName: String, trackId: Long): Int
}