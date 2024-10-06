package com.practicum.playlistmaker.ui.mediateka

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackListItemBinding
import com.practicum.playlistmaker.domain.search.models.Track

class FavoriteTracksAdapter(
    private val favoriteTrackClickListener: FavoriteTrackClickListener,
): RecyclerView.Adapter<FavoriteTrackViewHolder>() {

    var favoriteTracks: MutableList<Track> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTrackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FavoriteTrackViewHolder(TrackListItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteTrackViewHolder, position: Int) {

        holder.bind(favoriteTracks[position])
        holder.itemView.setOnClickListener { favoriteTrackClickListener.onItemClick(favoriteTracks[position]) }
    }

    override fun getItemCount(): Int = favoriteTracks.size

    fun interface FavoriteTrackClickListener {
        fun onItemClick(track: Track)
    }

}