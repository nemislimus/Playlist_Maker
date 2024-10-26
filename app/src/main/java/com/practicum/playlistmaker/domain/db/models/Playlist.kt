package com.practicum.playlistmaker.domain.db.models

data class Playlist(
    val id: Long,
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    var trackIdList: List<Long>,
    val tracksCount: Int = trackIdList.size
)

