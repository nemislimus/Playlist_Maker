package com.practicum.playlistmaker.domain.db.api

import android.net.Uri
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun saveCoverToPrivateStorage(uri: Uri, playlistName: String, renameFile: Boolean): String

    suspend fun deleteCoverFromPrivateStorage(coverPath: String)

    suspend fun savePlaylist(playlist: Playlist)

    suspend fun saveTrackToPlaylistPool(track: Track)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getPlaylistTracksFromPool(trackIds: List<Long>): Flow<List<Track>>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun addTrackIdToPlaylistById(playlistId: Long, trackId: Long): Int

    suspend fun deleteTrackFromPlaylist(track: Track, currentPlaylist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

}