package com.example.playlistmaker.ui.media.playlists

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import java.io.File

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    private val countTracksTextView: TextView = itemView.findViewById(R.id.countTracksTextView)

    fun bind(model: Playlist) {
        nameTextView.text = model.playlistName
        countTracksTextView.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_plurals,
            model.countTracks,
            model.countTracks
        )
        val file = File(model.coverPath)
        Glide.with(itemView)
            .load(file)
            .placeholder(R.drawable.empty_cover)
            .transform(RoundedCorners(dpToPx(itemView.context)))
            .into(coverImageView)
    }

    private fun dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            context.resources.displayMetrics
        ).toInt()
    }
}