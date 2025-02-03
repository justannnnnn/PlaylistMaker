package com.example.playlistmaker.ui.player

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import java.io.File

class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


    private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)
    private val nameTextView: TextView = itemView.findViewById(R.id.playlistNameTextView)
    private val countTracksTextView: TextView = itemView.findViewById(R.id.countTracksTextView)

    fun bind(model: Playlist){
        nameTextView.text = model.playlistName
        countTracksTextView.text = itemView.context.resources.getQuantityString(R.plurals.tracks_plurals, model.countTracks, model.countTracks)

        if (model.coverPath.isEmpty()){
            coverImageView.setImageResource(R.drawable.empty_cover)
        } else {
            val file = File(model.coverPath)
            coverImageView.load(file){
                transformations(RoundedCornersTransformation(8f))
            }
        }
    }
}