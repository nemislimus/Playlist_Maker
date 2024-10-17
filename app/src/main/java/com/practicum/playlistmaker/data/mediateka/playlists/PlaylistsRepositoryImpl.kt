package com.practicum.playlistmaker.data.mediateka.playlists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.createJsonFromTrackIdList
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.playlistDtoFromEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.playlistDtoToEntity
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto.Companion.playlistDtoFromPlaylist
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto.Companion.playlistDtoToPlaylist
import com.practicum.playlistmaker.domain.db.api.PlaylistsRepository
import com.practicum.playlistmaker.domain.db.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao
): PlaylistsRepository {

    override suspend fun savePlaylist(playlist: Playlist) {
        playlistsDao.savePlaylist(playlist.playlistDtoFromPlaylist().playlistDtoToEntity())
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = playlistsDao.getAllPlaylists().map { entity ->
            entity.playlistDtoFromEntity().playlistDtoToPlaylist()
        }
        emit(playlist)
    }

    override suspend fun getPlaylistByName(playlistName: String): Playlist? {
        return playlistsDao.getPlaylistByName(playlistName)?.playlistDtoFromEntity()?.playlistDtoToPlaylist()
    }

    override suspend fun addTrackIdToPlaylistByName(playlistName: String, trackId: Long): Int {
        val entityFromDb = playlistsDao.getPlaylistByName(playlistName)
        if (entityFromDb != null) {
            val trackIdList = entityFromDb.playlistDtoFromEntity().trackIdList
            if (!trackIdList.contains(trackId)) {
                trackIdList.add(trackId)
                playlistsDao.updatePlaylist(
                    upgradePlaylistEntity(
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

    private fun upgradePlaylistEntity(
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

    override suspend fun saveCoverToPrivateStorage(uri: Uri, coverIndex: Int): String {
        val context = getKoin().get<Context>()
        val coverFileNameString = createFileName(context, coverIndex)

        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_COVERS_FOLDER
        )

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val fileForSave = File(filePath, coverFileNameString)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(fileForSave)
        }

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return fileForSave.path
    }

    private fun createFileName(context: Context, coverIndex: Int): String {
        return  context.getString(R.string.cover_base)+coverIndex.toString()
    }

    companion object {
        const val ADD_TRACK_SUCCESS = 2
        const val ADD_TRACK_COLLISION = 1
        const val ADD_TRACK_PLAYLIST_NOT_EXIST = 0

        const val PLAYLIST_COVERS_FOLDER = "playlist_covers"
    }
}