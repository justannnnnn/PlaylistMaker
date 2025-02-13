package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.PlaylistAdapter
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val playlists = ArrayList<Playlist>()
    private val adapter: PlaylistAdapter = PlaylistAdapter()
    private var isClickAllowed = true
    private lateinit var onPlaylistClickDebounce: (Playlist, Track) -> Unit

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.backButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            val track = arguments?.getSerializable("selected_track") as? Track ?: return
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.empty_cover)
                .transform(RoundedCorners(dpToPx(8f)))
                .into(binding.coverImageView)

            binding.trackNameTextView.text = track.trackName
            binding.authorTextView.text = track.artistName
            binding.durationTextView.text = track.trackTime
            binding.albumGroup.visibility =
                if (track.collectionName == null) View.GONE else View.VISIBLE
            binding.albumTextView.text = track.collectionName ?: ""
            binding.yearTextView.text = track.releaseDate?.subSequence(0, 4) ?: ""
            binding.genreTextView.text = track.primaryGenreName
            binding.countryTextView.text = track.country

            track.previewUrl?.let { viewModel.preparePlayer(it, track.trackId) }

            viewModel.observePlayerState().observe(viewLifecycleOwner) {
                binding.playButton.isEnabled = it.isPlayButtonEnabled
                binding.playButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        it.buttonIcon
                    )
                )
                binding.timerTextView.text = it.progress
            }

            viewModel.observePlayerState().observe(viewLifecycleOwner) {
                binding.likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (it.isFavorite) R.drawable.like_active else R.drawable.like_inactive
                    )
                )
            }

            bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    binding.overlay.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            binding.addButton.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                viewModel.onAddButtonClicked()
                viewModel.observePlaylistsState().observe(viewLifecycleOwner) {
                    adapter.playlists.clear()
                    adapter.playlists.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }
            setupBottomSheet()

            adapter.onClickedPlaylist = { playlist ->
                onPlaylistClickDebounce(playlist, track)
            }

            binding.playButton.setOnClickListener {
                viewModel.onPlayButtonClicked()
            }

            binding.likeButton.setOnClickListener {
                viewModel.onFavoriteClicked(track)
            }
    }

    private fun setupBottomSheet() {
        binding.newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        adapter.playlists = playlists
        binding.playlistsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRV.adapter = adapter

        onPlaylistClickDebounce = { playlist, track ->
            if (clickDebounce()) {
                viewModel.onPlaylistClicked(playlist, track)
                viewModel.observeAddingInPlaylistState().observe(viewLifecycleOwner) {
                    it?.let {
                        val text = getString(
                            if (it) R.string.added_in_playlist else R.string.track_is_already_in_playlist,
                            playlist.playlistName
                        )
                        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance(track: Track) = PlayerFragment().apply {
            arguments = Bundle().apply {
                putSerializable("selected_track", track)
            }
        }
    }
}
