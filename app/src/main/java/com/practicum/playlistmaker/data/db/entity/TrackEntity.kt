package com.practicum.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.data.db.entity.TrackEntity.Companion.TABLE_NAME
import com.practicum.playlistmaker.data.search.models.TrackDto
import com.practicum.playlistmaker.domain.search.models.Track

@Entity(tableName = TABLE_NAME)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Long = 0L,
    @ColumnInfo(name = "track_id", typeAffinity = ColumnInfo.INTEGER, index = true)
    val trackId: Long,
    @ColumnInfo(name = "track_name", typeAffinity = ColumnInfo.TEXT)
    val trackName: String,
    @ColumnInfo(name = "artist_name", typeAffinity = ColumnInfo.TEXT)
    val artistName: String,
    @ColumnInfo(name = "track_time", typeAffinity = ColumnInfo.INTEGER)
    val trackTimeMillis: Long,
    @ColumnInfo(name = "picture_url", typeAffinity = ColumnInfo.TEXT)
    val artworkUrl100: String,
    @ColumnInfo(name = "sound_sample_url", typeAffinity = ColumnInfo.TEXT)
    val previewUrl: String,
    @ColumnInfo(name = "collection_name", typeAffinity = ColumnInfo.TEXT)
    val collectionName: String,
    @ColumnInfo(name = "release_date", typeAffinity = ColumnInfo.TEXT)
    val releaseDate: String,
    @ColumnInfo(name = "genre", typeAffinity = ColumnInfo.TEXT)
    val primaryGenreName: String,
    @ColumnInfo(name = "country", typeAffinity = ColumnInfo.TEXT)
    val country: String,
) {
    companion object {
        const val TABLE_NAME = "tracks_table"

        fun dtoToEntity(trackDto: TrackDto): TrackEntity {
            val trackArtworkUrl100 = trackDto.artworkUrl100 ?: ""
            val trackPreviewUrl = trackDto.previewUrl ?: ""
            return TrackEntity(
                trackId = trackDto.trackId,
                trackName = trackDto.trackName,
                artistName = trackDto.artistName,
                trackTimeMillis = trackDto.trackTimeMillis,
                artworkUrl100 = trackArtworkUrl100,
                previewUrl = trackPreviewUrl,
                collectionName = trackDto.collectionName,
                releaseDate = trackDto.releaseDate,
                primaryGenreName = trackDto.primaryGenreName,
                country = trackDto.country,
            )
        }

        fun entityToTrack(trackEntity: TrackEntity): Track {
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
                isFavorite = true
            )
        }

        fun trackToEntity(track: Track): TrackEntity {
            return TrackEntity(
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
            )
        }

    }
}
