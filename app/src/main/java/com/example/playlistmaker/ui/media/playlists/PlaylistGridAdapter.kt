package com.example.playlistmaker.ui.media.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist

class PlaylistGridAdapter: RecyclerView.Adapter<PlaylistGridViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item_grid, parent, false)
        return PlaylistGridViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}