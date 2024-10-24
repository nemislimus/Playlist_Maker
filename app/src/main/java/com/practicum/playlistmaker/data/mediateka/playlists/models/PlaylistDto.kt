package com.practicum.playlistmaker.data.mediateka.playlists.models

import android.net.Uri
import com.practicum.playlistmaker.domain.db.models.Playlist


data class PlaylistDto(
    val id: Long,
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: Uri,
    val trackIdList: MutableList<Long>,
    val tracksCount: Int = trackIdList.size
) {

    companion object {

        fun PlaylistDto.playlistDtoToPlaylist(): Playlist {
            return Playlist(
                id = this.id,
                playlistName = this.playlistName,
                playlistDescription = this.playlistDescription,
                coverPath = this.coverPath.toString(),
                trackIdList = this.trackIdList,
            )
        }

        fun Playlist.playlistDtoFromPlaylist(): PlaylistDto {
            return PlaylistDto(
                id = this.id,
                playlistName = this.playlistName,
                playlistDescription = this.playlistDescription,
                coverPath = Uri.parse(this.coverPath),
                trackIdList = this.trackIdList.toMutableList(),
            )
        }
    }
}
