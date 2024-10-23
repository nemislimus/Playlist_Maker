package com.practicum.playlistmaker.data.db.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity.Companion.PLAYLISTS_TABLE_NAME
import com.practicum.playlistmaker.data.mediateka.playlists.models.PlaylistDto
import java.lang.reflect.Type

@Entity(
    tableName = PLAYLISTS_TABLE_NAME,
    indices = [Index(value = ["playlist_name"], unique = true)]
)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "playlist_name")
    val playlistName: String,

    @ColumnInfo(name = "playlist_description")
    val playlistDescription: String,

    @ColumnInfo(name = "cover_path")
    val coverPath: String,

    @ColumnInfo(name = "track_ids_list")
    val trackIdList: String,

    @ColumnInfo(name = "track_count")
    val tracksCount: Int


) {
    companion object {
        const val PLAYLISTS_TABLE_NAME = "playlist_table"

        fun PlaylistDto.playlistDtoToEntity(): PlaylistEntity {
            return PlaylistEntity(
                id = this.id,
                playlistName = this.playlistName,
                playlistDescription = this.playlistDescription,
                coverPath = this.coverPath.toString(),
                trackIdList = createJsonFromTrackIdList(this.trackIdList),
                tracksCount = this.trackIdList.size,
            )
        }

        fun PlaylistEntity.playlistDtoFromEntity(): PlaylistDto {
            return PlaylistDto(
                id = this.id,
                playlistName = this.playlistName,
                playlistDescription = this.playlistDescription,
                coverPath = Uri.parse(this.coverPath),
                trackIdList = createTrackIdListFromJson(this.trackIdList).toMutableList(),
            )
        }

        fun createJsonFromTrackIdList(list: List<Long>): String {
            return Gson().toJson(list)
        }

        private fun createTrackIdListFromJson(jsonValue: String): List<Long> {
            return Gson().fromJson(jsonValue, trackIdListType)
        }

        private val trackIdListType: Type? = object : TypeToken<List<Long>>() {}.type
    }
}
