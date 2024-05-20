package com.practicum.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

open class BaseTrackViewHolder(view: View): RecyclerView.ViewHolder(view) {
    open fun bind(model: Track) {
    }
}

class ClearHistoryButtonViewHolder(parent: ViewGroup): BaseTrackViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.clear_history_btn_item, parent, false)
) {

    val clearBtn: Button = itemView.findViewById(R.id.btnClearHistory)

    override fun bind(model: Track) {
        Log.d("HISTORY_LOG","Объект заглушка создан: ID ${model.trackId}")
    }

}