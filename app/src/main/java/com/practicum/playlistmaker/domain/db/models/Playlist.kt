package com.practicum.playlistmaker.domain.db.models


data class Playlist(
    val playlistName: String,
    val playlistDescription: String,
    val coverPath: String,
    val trackIdList: List<Long>,
    val tracksCount: Int = trackIdList.size
)

