package com.practicum.playlistmaker.presentation.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.entities.Track

abstract class BaseTrackViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(model: Track)
}

class ClearHistoryButtonViewHolder(parent: ViewGroup): BaseTrackViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.clear_history_btn_item, parent, false)
) {

    val clearBtn: Button = itemView.findViewById(R.id.btnClearHistory)

    override fun bind(model: Track) {
        Log.d("HISTORY_LOG","Объект заглушка создан: ID ${model.trackId}")
    }

}