package com.practicum.playlistmaker.presentation.ui.search

import android.content.Context
import android.util.TypedValue
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackListItemBinding
import com.practicum.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TrackListItemBinding) : BaseTrackViewHolder(binding.root) {

    override fun bind(model: Track) {
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        binding.tvTrackName.text = model.trackName
        binding.tvTrackArtistName.text = model.artistName
        binding.tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusInPx))
            .into(binding.ivTrackImage)
    }

    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }
    }
}