package com.example.playlistmaker.ui.player.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra("selected_track") as Track
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.empty_cover)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(binding.coverImageView)
        binding.trackNameTextView.text = track.trackName
        binding.authorTextView.text = track.artistName
        binding.durationTextView.text = track.trackTime
        if (track.collectionName == null) binding.albumGroup.visibility = View.GONE
        else {
            binding.albumGroup.visibility = View.VISIBLE
            binding.albumTextView.text = track.collectionName
        }
        binding.yearTextView.text = track.releaseDate?.subSequence(0, 4) ?: ""
        binding.genreTextView.text = track.primaryGenreName
        binding.countryTextView.text = track.country

        track.previewUrl?.let { viewModel.preparePlayer(it) }

        viewModel.observePlayerState().observe(this){
            binding.playButton.isEnabled = it.isPlayButtonEnabled
            binding.playButton.setImageDrawable(getDrawable(it.buttonIcon))
            binding.timerTextView.text = it.progress
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}