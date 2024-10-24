package com.practicum.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.data.db.entity.PlaylistPoolTrackEntity.Companion.PLAYLIST_POOL_TRACK_TABLE_NAME
import com.practicum.playlistmaker.domain.search.models.Track

@Entity(tableName = PLAYLIST_POOL_TRACK_TABLE_NAME)
data class PlaylistPoolTrackEntity(

    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Long,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTimeMillis: Long,

    @ColumnInfo(name = "picture_url")
    val artworkUrl100: String,

    @ColumnInfo(name = "sound_sample_url")
    val previewUrl: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String,

    @ColumnInfo(name = "genre")
    val primaryGenreName: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
) {
    companion object {
        const val PLAYLIST_POOL_TRACK_TABLE_NAME = "playlist_pool_tracks_table"

        fun entityToTrack(trackEntity: PlaylistPoolTrackEntity): Track {
            return Track(
                trackId = trackEntity.trackId,
                trackName = trackEntity.trackName,
                artistName = trackEntity.artistName,
                trackTimeMillis = trackEntity.trackTimeMillis,
                artworkUrl100 = trackEntity.artworkUrl100,
                previewUrl = trackEntity.previewUrl,
                collectionName = trackEntity.collectionName,
                releaseDate = trackEntity.releaseDate,
                primaryGenreName = trackEntity.primaryGenreName,
                country = trackEntity.country,
                isFavorite = trackEntity.isFavorite
            )
        }

        fun trackToEntity(track: Track): PlaylistPoolTrackEntity {
            return PlaylistPoolTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                previewUrl = track.previewUrl,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                isFavorite = track.isFavorite,
            )
        }
    }
}
