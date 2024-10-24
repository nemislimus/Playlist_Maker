package com.practicum.playlistmaker.data.mediateka.playlists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.db.dao.PlaylistTracksDao
import com.practicum.playlistmaker.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.createJsonFromTrackIdList
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.playlistDtoFromEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.playlistDtoToEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistPoolTrackEntity
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto.Companion.playlistDtoFromPlaylist
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto.Companion.playlistDtoToPlaylist
import com.practicum.playlistmaker.domain.db.api.PlaylistsRepository
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val playlistTracksDao: PlaylistTracksDao,
): PlaylistsRepository {

    override suspend fun savePlaylist(playlist: Playlist) {
        playlistsDao.savePlaylist(playlist.playlistDtoFromPlaylist().playlistDtoToEntity())
    }

    override suspend fun saveTrackToPlaylistPool(track: Track) {
        playlistTracksDao.saveTrackToPlaylistPool(PlaylistPoolTrackEntity.trackToEntity(track))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = playlistsDao.getAllPlaylists().map { entity ->
            entity.playlistDtoFromEntity().playlistDtoToPlaylist()
        }
        emit(playlist)
    }

    override fun getPlaylistTracksFromPool(trackIds: List<Long>): Flow<List<Track>> = flow {
        val trackList = playlistTracksDao.getAllTracksFromPlaylistPool().map { entity ->
            PlaylistPoolTrackEntity.entityToTrack(entity)
        }.filter { track -> trackIds.contains(track.trackId) }
        emit(trackList)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistsDao.getPlaylistById(playlistId)?.playlistDtoFromEntity()?.playlistDtoToPlaylist()
    }

    override suspend fun addTrackIdToPlaylistById(playlistId: Long, trackId: Long): Int {
        val entityFromDb = playlistsDao.getPlaylistById(playlistId)
        if (entityFromDb != null) {
            val trackIdList = entityFromDb.playlistDtoFromEntity().trackIdList
            if (!trackIdList.contains(trackId)) {
                trackIdList.add(trackId)
                playlistsDao.updatePlaylist(
                    updatePlaylistEntityOnAddTrack(
                        entityFromDb,
                        trackIdList
                    )
                )
                return ADD_TRACK_SUCCESS
            } else {
                return ADD_TRACK_COLLISION
            }
        }
        return ADD_TRACK_PLAYLIST_NOT_EXIST
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        deleteCoverFromPrivateStorage(playlist.coverPath)
        playlistsDao.deletePlaylist(playlist.id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsDao.updatePlaylist(playlist.playlistDtoFromPlaylist().playlistDtoToEntity())
    }

    override suspend fun deleteCoverFromPrivateStorage(coverPath: String) {
        val coverFile = File(coverPath)
        if (coverFile.exists()) coverFile.delete()
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, currentPlaylist: Playlist) {
        val dto = currentPlaylist.playlistDtoFromPlaylist()
        playlistsDao.updatePlaylist(createPlaylistEntityOnDeleteTrack(dto, track.trackId))

        if (!otherPlaylistHasTrack(track.trackId)) playlistTracksDao
            .deleteTrack(PlaylistPoolTrackEntity.trackToEntity(track))
    }

    private suspend fun otherPlaylistHasTrack(trackId: Long): Boolean {
        return playlistsDao.getAllPlaylists().flatMap {
            playlist -> playlist.playlistDtoFromEntity().trackIdList
        }.contains(trackId)
    }

    private fun updatePlaylistEntityOnAddTrack(
        entity: PlaylistEntity,
        newTrackIdList: MutableList<Long>
    ): PlaylistEntity {
        return PlaylistEntity(
            id = entity.id,
            playlistName = entity.playlistName,
            playlistDescription = entity.playlistDescription,
            coverPath = entity.coverPath,
            trackIdList = createJsonFromTrackIdList(newTrackIdList),
            tracksCount = newTrackIdList.size
        )
    }

    private fun createPlaylistEntityOnDeleteTrack(
        dto: PlaylistDto,
        trackId: Long,
    ): PlaylistEntity {
        dto.trackIdList.remove(trackId)
        return PlaylistEntity(
            id = dto.id,
            playlistName = dto.playlistName,
            playlistDescription = dto.playlistDescription,
            coverPath = dto.coverPath.toString(),
            trackIdList = createJsonFromTrackIdList(dto.trackIdList),
            tracksCount = dto.trackIdList.size
        )
    }

    override suspend fun saveCoverToPrivateStorage(uri: Uri, playlistName: String, renameFile: Boolean): String {
        val quality: Int = if (renameFile) 100 else 30
        val context = getKoin().get<Context>()
        val coverFileNameString = createFileName(context, playlistName)

        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_COVERS_FOLDER
        )

        if (!filePath.exists()) filePath.mkdirs()

        val fileForSave = File(filePath, coverFileNameString)
        if (fileForSave.exists()) fileForSave.delete()

        val inputStream = if (renameFile) {
            withContext(Dispatchers.IO) {
                FileInputStream(File(uri.toString()))
            }
        } else {
            context.contentResolver.openInputStream(uri)
        }

        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(fileForSave)
        }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        return fileForSave.path
    }

    private fun createFileName(context: Context, playlistName: String): String {
        return  context.getString(R.string.cover_base)+playlistName
    }

    companion object {
        const val ADD_TRACK_SUCCESS = 2
        const val ADD_TRACK_COLLISION = 1
        const val ADD_TRACK_PLAYLIST_NOT_EXIST = 0

        const val PLAYLIST_COVERS_FOLDER = "playlist_covers"
    }
}