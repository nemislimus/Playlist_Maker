package com.practicum.playlistmaker.ui.search

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackListItemBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackListItemBinding) : BaseTrackViewHolder(binding.root) {

    override fun bind(model: Track) {
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        with(binding) {
            tvTrackName.text = model.trackName
            tvTrackArtistName.text = model.artistName
            tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusInPx))
            .into(binding.ivTrackImage)
    }

}