package com.example.playlistmaker.ui.media.playlists.playlist.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.playlists.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private val playlistTracks = ArrayList<Track>()
    private val adapter: TrackAdapter = TrackAdapter()
    private lateinit var playlist: Playlist

    private val viewModel: PlaylistViewModel by viewModel<PlaylistViewModel>()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onLongTrackClick: (Track) -> Unit
    private lateinit var removeTrackDialog: MaterialAlertDialogBuilder
    private lateinit var removePlaylistDialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        playlist = arguments?.getSerializable("selected_playlist") as? Playlist ?: return

        renderPlaylist(playlist)

        binding.shareButton.setOnClickListener {
            if (playlist.countTracks == 0) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.no_tracks_to_share),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(viewModel.sharePlaylistTracks(playlist))
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
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

        binding.moreButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        setupBottomSheet(playlist)

        onTrackClickDebounce = { track ->
            val bundle = Bundle().apply {
                putSerializable("selected_track", track)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment, bundle)
        }
        onLongTrackClick = { track ->
            removeTrackDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.delete_track))
                .setMessage(requireContext().getString(R.string.you_sure_to_delete_track))
                .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ ->
                }
                .setNegativeButton(requireContext().getString(R.string.delete)) { _, _ ->
                    viewModel.deleteTrackFromPlaylist(track, playlist)
                }

            removeTrackDialog.show()
        }

        adapter.onClickedTrack = { track ->
            onTrackClickDebounce(track)
        }
        adapter.onLongClickedTrack = { track ->
            onLongTrackClick(track)
        }
        buildRV()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylist(playlist.playlistId)
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            renderPlaylist(it)
        }
    }


    private fun render(tracks: List<Track>?) {
        binding.tracksCountTextView.text = getCountTracksText(tracks?.size ?: 0)
        binding.durationTextView.text = getDuration(tracks ?: emptyList())
        if (tracks.isNullOrEmpty()) {
            binding.placeholderTextView.visibility = View.VISIBLE
            binding.tracksRV.visibility = View.GONE
        } else {
            binding.placeholderTextView.visibility = View.GONE
            binding.tracksRV.visibility = View.VISIBLE
            adapter.tracks.clear()
            adapter.tracks.addAll(tracks.reversed())
            adapter.notifyDataSetChanged()
        }
    }

    private fun renderPlaylist(playlist: Playlist?) {
        playlist?.let {
            viewModel.getTracks(playlist.tracks)
            viewModel.observeTracksState().observe(viewLifecycleOwner) {
                render(it)
            }

            if (playlist.coverPath.isEmpty()) {
                binding.playlistCover.setBackgroundResource(R.drawable.empty_cover)
            } else {
                val file = File(playlist.coverPath)

                Coil.imageLoader(requireContext()).enqueue(
                    ImageRequest.Builder(requireContext())
                        .data(file)
                        .target(
                            onSuccess = { drawable ->
                                binding.playlistCover.background = drawable
                            },
                            onError = {
                                binding.playlistCover.setBackgroundResource(R.drawable.empty_cover)
                            }
                        )
                        .build()
                )
            }

            binding.playlistNameTextView.text = playlist.playlistName
            if (playlist.playlistDescription.isEmpty()) {
                binding.playlistDescTextView.visibility = View.GONE
            } else {
                binding.playlistDescTextView.text = playlist.playlistDescription
            }
        }
    }

    private fun buildRV() {
        adapter.tracks = playlistTracks
        binding.tracksRV.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRV.adapter = adapter
    }

    private fun setupBottomSheet(playlist: Playlist) {
        binding.playlistInfoInc.playlistNameTextView.text = playlist.playlistName
        binding.playlistInfoInc.countTracksTextView.text = requireContext().resources
            .getQuantityString(
                R.plurals.tracks_plurals,
                playlist.countTracks,
                playlist.countTracks
            )

        if (playlist.coverPath.isEmpty()) {
            binding.playlistInfoInc.coverImageView.setImageResource(R.drawable.empty_cover)
        } else {
            val file = File(playlist.coverPath)
            binding.playlistInfoInc.coverImageView.load(file) {
                transformations(RoundedCornersTransformation(8f))
            }
        }

        binding.shareButtonBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (playlist.countTracks == 0) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.no_tracks_to_share),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(viewModel.sharePlaylistTracks(playlist))
            }
        }
        binding.editButtonBottomSheet.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("editing_playlist", playlist)
            }
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundle
            )
        }
        binding.deleteButtonBottomSheet.setOnClickListener {
            removePlaylistDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.delete_playlist))
                .setMessage(requireContext().getString(R.string.want_to_delete_playlist))
                .setNeutralButton(requireContext().getString(R.string.no)) { _, _ ->
                }
                .setNegativeButton(requireContext().getString(R.string.yes)) { _, _ ->
                    viewModel.deletePlaylist(playlist)
                    findNavController().navigateUp()
                }
            removePlaylistDialog.show()
        }
    }

    private fun getDuration(tracks: List<Track>): String {
        val duration = tracks.sumOf {
            it.trackTime.split(":").firstOrNull()?.toIntOrNull() ?: 0
        }
        return requireContext().resources.getQuantityString(
            R.plurals.minutes_plurals,
            duration,
            duration
        )
    }

    private fun getCountTracksText(count: Int): String {
        return requireContext().resources.getQuantityString(R.plurals.tracks_plurals, count, count)
    }
}