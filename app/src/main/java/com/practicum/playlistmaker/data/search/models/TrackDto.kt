package com.practicum.playlistmaker.data.search.models

import com.practicum.playlistmaker.domain.search.models.Track

data class TrackDto(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
) {
    companion object {

        fun convertToTrack(trackDto: TrackDto): Track {
            val trackArtworkUrl100 = trackDto.artworkUrl100 ?: ""
            val trackPreviewUrl = trackDto.previewUrl ?: ""
            return Track(
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

        fun trackToTrackDto(track: Track): TrackDto {
            return TrackDto(
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
