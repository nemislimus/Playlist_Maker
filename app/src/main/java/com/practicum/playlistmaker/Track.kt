package com.practicum.playlistmaker

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
) {
    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
