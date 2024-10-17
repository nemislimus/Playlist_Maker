package com.practicum.playlistmaker.ui.mediateka

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistListItemBinding
import com.practicum.playlistmaker.domain.db.models.Playlist

class PlaylistViewHolder(private val binding: PlaylistListItemBinding) : RecyclerView.ViewHolder(binding.root) {

     fun bind(model: Playlist) {
        with(binding) {
            if (model.coverPath.isNotBlank()) ivPlaylistCover.setImageURI(Uri.parse(model.coverPath))
            tvPlaylistTitle.text = model.playlistName
            val tracksCount = "${model.tracksCount} ${getCorrectWord(model.tracksCount)}"
            tvTracksCount.text = tracksCount
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