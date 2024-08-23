package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val cover = findViewById<ImageView>(R.id.coverImageView)
        val trackName = findViewById<TextView>(R.id.trackNameTextView)
        val author = findViewById<TextView>(R.id.authorTextView)
        val duration = findViewById<TextView>(R.id.durationTextView)

        val albumGroup = findViewById<Group>(R.id.albumGroup)
        val album = findViewById<TextView>(R.id.albumTextView)

        val year = findViewById<TextView>(R.id.yearTextView)
        val genre = findViewById<TextView>(R.id.genreTextView)
        val country = findViewById<TextView>(R.id.countryTextView)

        val sp = getSharedPreferences(App.PLAYLIST_PREFERENCES, MODE_PRIVATE)
        val trackStr = sp.getString(App.NEW_VAL_KEY, null)
        val track = Gson().fromJson(trackStr, Track::class.java)

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.empty_cover_big)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(cover)
        trackName.text = track.trackName
        author.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format((track.trackTime).toLong())
        if (track.collectionName == null) albumGroup.visibility = View.GONE
        else {
            albumGroup.visibility = View.VISIBLE
            album.text = track.collectionName
        }
        year.text = track.releaseDate.subSequence(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country


    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}