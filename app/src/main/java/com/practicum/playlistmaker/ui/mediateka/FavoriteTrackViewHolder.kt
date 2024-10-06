package com.practicum.playlistmaker.ui.mediateka

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackListItemBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteTrackViewHolder(private val binding: TrackListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

     fun bind(model: Track) {
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        with(binding) {
            tvTrackName.text = model.trackName
            tvTrackArtistName.text = model.artistName
            tvTrackTime.text = dateFormat.format(model.trackTimeMillis)
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusInPx))
            .into(binding.ivTrackImage)
    }

}