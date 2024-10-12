package com.practicum.playlistmaker.ui.mediateka

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.BsPlaylistListItemBinding
import com.practicum.playlistmaker.databinding.PlaylistListItemBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.player.PlayerPlaylistViewHolder


class PlaylistsAdapter(
    private val isPlayerPlaylist: Boolean,
    private val itemClickListener: PlaylistClickListener?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val playlists: MutableList<Playlist> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            TYPE_PLAYLIST -> PlaylistViewHolder(
                PlaylistListItemBinding.inflate(layoutInflater, parent, false)
            )
            TYPE_PLAYER_PLAYLIST -> PlayerPlaylistViewHolder(
                BsPlaylistListItemBinding.inflate(layoutInflater, parent, false)
            )
            else -> throw IllegalAccessException("Unknown ViewHolder type for create")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is PlaylistViewHolder -> {
                holder.bind(playlists[position])
            }

            is PlayerPlaylistViewHolder -> {
                holder.bind(playlists[position])
                holder.itemView.setOnClickListener { itemClickListener?.onItemClick(playlists[position]) }
            }
            else -> throw IllegalAccessException("Unknown ViewHolder type for binding")
        }
    }

    override fun getItemViewType(position: Int): Int = if (isPlayerPlaylist) {
        TYPE_PLAYER_PLAYLIST
    } else {
        TYPE_PLAYLIST
    }

    override fun getItemCount(): Int = playlists.size

    fun interface PlaylistClickListener {
        fun onItemClick(playlist: Playlist)
    }

    companion object {
        private const val TYPE_PLAYLIST = 0
        private const val TYPE_PLAYER_PLAYLIST = 1
    }

}