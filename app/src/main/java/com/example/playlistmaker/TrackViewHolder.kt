package com.example.playlistmaker

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
    private val durationTextView: TextView = itemView.findViewById(R.id.durantionTextView)
    private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)

    fun bind(model: Track){
        trackTextView.text = model.trackName
        authorTextView.text = model.artistName
        durationTextView.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.empty_cover)
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(coverImageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}