package com.example.playlistmaker

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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import kotlinx.coroutines.Delay
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView
    private var mainThreadHandler: Handler? = null
    private var durationTrack = 30L

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
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
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format((track.trackTime).toLong())
        if (track.collectionName == null) albumGroup.visibility = View.GONE
        else {
            albumGroup.visibility = View.VISIBLE
            album.text = track.collectionName
        }
        year.text = track.releaseDate.subSequence(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country

        playButton = findViewById<ImageButton>(R.id.playButton)
        preparePlayer(track.previewUrl)
        playButton.setOnClickListener { playbackControl() }

        timer = findViewById(R.id.timerTextView)
        mainThreadHandler = Handler(Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
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
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
        playerState = STATE_PLAYING
        startTimer(durationTrack)
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        playButton.setImageDrawable(getDrawable(R.drawable.play))
        playerState = STATE_PAUSED

    }

    private fun playbackControl(){
        when(playerState){
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED ->{
                startPlayer()
                durationTrack = 30L
                startTimer(durationTrack)
            }
            STATE_PAUSED -> startPlayer()
        }
    }

    private fun startTimer(duration: Long){
        var startTime = System.currentTimeMillis()
        mainThreadHandler?.post(createUpdateTimerTask(startTime, duration * 1000L))
    }

    private fun createUpdateTimerTask(startTime: Long, duration: Long): Runnable{
        return object : Runnable{
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                val remain = duration - elapsedTime

                if (playerState == STATE_PLAYING){
                    if (remain > 0){
                        durationTrack = remain / 1000L
                        val seconds = remain / 1000L
                        timer.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
                        mainThreadHandler?.postDelayed(this, 1000L)
                    }
                    else {
                        timer.text = "00:00"

                    }
                }
            }
        }
    }



    companion object{
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}