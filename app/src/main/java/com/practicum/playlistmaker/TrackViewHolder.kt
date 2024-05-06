package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : BaseTrackViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
) {
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackArtistName: TextView
    private val trackTime: TextView

    init {
        trackImage = itemView.findViewById(R.id.iv_trackImage)
        trackName = itemView.findViewById(R.id.tv_trackName)
        trackArtistName = itemView.findViewById(R.id.tv_trackArtistName)
        trackTime = itemView.findViewById(R.id.tv_trackTime)
    }

    override fun bind(model: Track) {
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        trackName.text = model.trackName
        trackArtistName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis) // фишка из теории

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusInPx))
            .into(trackImage)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}