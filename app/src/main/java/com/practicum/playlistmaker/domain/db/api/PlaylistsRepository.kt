package com.practicum.playlistmaker.domain.db.api

import android.content.Context
import android.net.Uri
import com.practicum.playlistmaker.domain.db.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun saveCoverToPrivateStorage(context: Context, uri: Uri, coverIndex: Int): String

    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistByName(playlistName: String): Playlist?

    suspend fun addTrackToPlaylistByName(playlistName: String, trackId: Long): Int
}