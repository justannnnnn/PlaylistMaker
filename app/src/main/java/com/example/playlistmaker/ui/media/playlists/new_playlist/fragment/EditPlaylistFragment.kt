package com.example.playlistmaker.ui.media.playlists.new_playlist.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.Coil
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.ui.media.playlists.new_playlist.view_model.EditPlaylistViewModel
import com.example.playlistmaker.ui.media.playlists.new_playlist.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel: NewPlaylistViewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = arguments?.getSerializable("editing_playlist") as? Playlist ?: return

        binding.titleTextView.text = requireContext().getString(R.string.edit)
        binding.createPlaylistButton.text = requireContext().getString(R.string.save)
        binding.nameInput.setText(playlist.playlistName)
        binding.descInput.setText(playlist.playlistDescription)

        coverPath = playlist.coverPath
        if (playlist.coverPath.isNotEmpty()) {
            val file = File(playlist.coverPath)

            Coil.imageLoader(requireContext()).enqueue(
                ImageRequest.Builder(requireContext())
                    .data(file)
                    .transformations(RoundedCornersTransformation(80f))
                    .target(
                        onSuccess = { drawable ->
                            binding.photoPickupLL.background = drawable
                            binding.placeholderIV.isVisible = false
                        }
                    )
                    .build()
            )
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createPlaylistButton.setOnClickListener {
            val newPlaylist = Playlist(
                playlistId = playlist.playlistId,
                playlistName = binding.nameInput.text.toString(),
                playlistDescription = binding.descInput.text.toString(),
                coverPath = coverPath,
                tracks = playlist.tracks,
                countTracks = playlist.countTracks
            )
            viewModel.onCreateButtonClicked(newPlaylist)
            findNavController().navigateUp()
        }

    }
}