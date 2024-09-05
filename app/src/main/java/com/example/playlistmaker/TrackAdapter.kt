package com.example.playlistmaker
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder>(){

    var tracks = ArrayList<Track>()
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val sp = it.context.getSharedPreferences("playlist_prefs", 0)
                sp.edit()
                    .putString(App.NEW_VAL_KEY, Gson().toJson(tracks[position]))
                    .apply()
                val intent = Intent(it.context, PlayerActivity::class.java)
                intent.putExtra("selected_track", tracks[position])
                it.context.startActivity(intent)
            }
        }
    }

    private fun clickDebounce() : Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


}