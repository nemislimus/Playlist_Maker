package com.practicum.playlistmaker.domain.db.impl

import android.net.Uri
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.api.PlaylistsRepository
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
): PlaylistsInteractor {

    override suspend fun savePlaylist(playlist: Playlist) =
        repository.savePlaylist(playlist)

    override suspend fun saveTrackToPlaylistPool(track: Track) =
        repository.saveTrackToPlaylistPool(track)

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        repository.getAllPlaylists()

    override fun getPlaylistTracksFromPool(trackIds: List<Long>): Flow<List<Track>> =
        repository.getPlaylistTracksFromPool(trackIds)

    override suspend fun getPlaylistById(playlistId: Long): Playlist? =
        repository.getPlaylistById(playlistId)

    override suspend fun addTrackIdToPlaylistById(playlistId: Long, trackId: Long): Int =
        repository.addTrackIdToPlaylistById(playlistId, trackId)

    override suspend fun deleteTrackFromPlaylist(track: Track, currentPlaylist: Playlist) =
        repository.deleteTrackFromPlaylist(track, currentPlaylist)

    override suspend fun deletePlaylist(playlist: Playlist) = repository.deletePlaylist(playlist)

    override suspend fun updatePlaylist(playlist: Playlist) =
        repository.updatePlaylist(playlist)

    override suspend fun saveCoverToPrivateStorage(
        uri: Uri,
        playlistName: String,
        renameFile: Boolean
    ): String {
        return repository.saveCoverToPrivateStorage(uri, playlistName, renameFile)
    }

    override suspend fun deleteCoverFromPrivateStorage(coverPath: String) =
        repository.deleteCoverFromPrivateStorage(coverPath)
}