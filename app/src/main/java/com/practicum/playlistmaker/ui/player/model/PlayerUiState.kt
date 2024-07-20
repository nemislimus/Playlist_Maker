package com.practicum.playlistmaker.ui.player.model

import com.bumptech.glide.load.resource.bitmap.RoundedCorners

data class PlayerUiState(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackDuration: String,
    val coverLink: String,
    val coverCornerRadius: RoundedCorners,
    val soundPreview: String,
    val hasCollection: Boolean,
    val collectionName: String,
    val releaseDate: String,
    val genreName: String,
    val country: String,
)
