package com.example.playlistmaker.presentation.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView
    private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val updateTimeRunnable = object: Runnable{
        override fun run() {
            if (playerState == STATE_PLAYING){
                val currentPos = mediaPlayer.currentPosition
                timer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPos)
                mainThreadHandler.postDelayed(this, 1000)
            }
        }
    }

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



        val track = intent.getSerializableExtra("selected_track") as Track
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.empty_cover)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(cover)
        trackName.text = track.trackName
        author.text = track.artistName
        duration.text = track.trackTime
        if (track.collectionName == null) albumGroup.visibility = View.GONE
        else {
            albumGroup.visibility = View.VISIBLE
            album.text = track.collectionName
        }
        year.text = track.releaseDate?.subSequence(0, 4) ?: ""
        genre.text = track.primaryGenreName
        country.text = track.country

        playButton = findViewById(R.id.playButton)

        track.previewUrl?.let { preparePlayer(it) }
        playButton.setOnClickListener { playbackControl() }
        timer = findViewById(R.id.timerTextView)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun preparePlayer(url: String){
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play))
            mainThreadHandler.removeCallbacks(updateTimeRunnable)
            timer.text = getString(R.string.zero_time)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
        playerState = STATE_PLAYING
        mainThreadHandler.post(updateTimeRunnable)
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        playButton.setImageDrawable(getDrawable(R.drawable.play))
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
        playerState = STATE_PAUSED

    }

    private fun playbackControl(){
        when(playerState){
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }



    companion object{
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}