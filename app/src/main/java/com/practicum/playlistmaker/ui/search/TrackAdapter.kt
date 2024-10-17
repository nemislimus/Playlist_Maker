package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ClearHistoryBtnItemBinding
import com.practicum.playlistmaker.databinding.TrackListItemBinding
import com.practicum.playlistmaker.domain.search.models.Track

class TrackAdapter(
    private val trackClickListener: ItemClickListener,
    private val buttonClickListener: ItemClickListener
): RecyclerView.Adapter<BaseTrackViewHolder>() {

    val tracks: MutableList<Track> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTrackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            TYPE_CLEAR_BUTTON -> ClearHistoryButtonViewHolder(ClearHistoryBtnItemBinding.inflate(layoutInflater, parent, false))
            TYPE_TRACK -> TrackViewHolder(TrackListItemBinding.inflate(layoutInflater, parent, false))
            else -> throw IllegalAccessException("Unknown ViewHolder type for create")
        }
    }

    override fun onBindViewHolder(holder: BaseTrackViewHolder, position: Int) {
        when(holder) {
            is TrackViewHolder -> {
                holder.bind(tracks[position])
                holder.itemView.setOnClickListener { trackClickListener.onItemClick(tracks[position]) }
            }

            is ClearHistoryButtonViewHolder -> {
                holder.bind(tracks[position])
                holder.clearBtn.setOnClickListener { buttonClickListener.onItemClick(track = tracks[position]) }
            }
            else -> throw IllegalAccessException("Unknown ViewHolder type for binding")
        }
    }

    override fun getItemCount(): Int = tracks.size

    override fun getItemViewType(position: Int): Int = if (tracks[position].trackId == ID_MARK) {
        TYPE_CLEAR_BUTTON
    } else {
        TYPE_TRACK
    }

    fun interface ItemClickListener {
        fun onItemClick(track: Track)
    }

    companion object {
        private const val TYPE_TRACK = 0
        private const val TYPE_CLEAR_BUTTON = 1

        private const val ID_MARK: Long = -1
    }
}