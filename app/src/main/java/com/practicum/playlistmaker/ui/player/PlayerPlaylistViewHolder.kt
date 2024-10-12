package com.practicum.playlistmaker.ui.player

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.BsPlaylistListItemBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.dpToPx

class PlayerPlaylistViewHolder(private val binding: BsPlaylistListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        with(binding) {
            tvPlaylistName.text = model.playlistName
            val tracksCount = "${model.tracksCount} ${getCorrectWord(model.tracksCount)}"
            tvTracksCount.text = tracksCount
        }

        if(model.coverPath.isNotBlank()) {
            Glide.with(itemView)
                .load(model.coverPath)
                .transform(
                    CenterCrop(), RoundedCorners(cornerRadiusInPx)
                )
                .placeholder(R.drawable.ic_placeholder_track_image)
                .into(binding.ivPlaylistImage)
        }
    }

    private fun getCorrectWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> itemView.context.getString(R.string.track_1)
            count % 10 in 2..4 && count % 100 !in 12..14 -> itemView.context.getString(R.string.track_24)
            else -> itemView.context.getString(R.string.track_other)
        }
    }
}