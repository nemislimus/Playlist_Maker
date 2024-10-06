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
            val trackId = trackDto.trackId ?: 0L
            val trackName = trackDto.trackName ?: ""
            val artistName = trackDto.artistName ?: ""
            val trackTimeMillis = trackDto.trackTimeMillis ?: 0L
            val collectionName = trackDto.collectionName ?: ""
            val releaseDate = trackDto.releaseDate ?: ""
            val primaryGenreName = trackDto.primaryGenreName ?: ""
            val country = trackDto.country ?: ""
            val trackArtworkUrl100 = trackDto.artworkUrl100 ?: ""
            val trackPreviewUrl = trackDto.previewUrl ?: ""

            return Track(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                trackTimeMillis = trackTimeMillis,
                artworkUrl100 = trackArtworkUrl100,
                previewUrl = trackPreviewUrl,
                collectionName = collectionName,
                releaseDate = releaseDate,
                primaryGenreName = primaryGenreName,
                country = country,
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
