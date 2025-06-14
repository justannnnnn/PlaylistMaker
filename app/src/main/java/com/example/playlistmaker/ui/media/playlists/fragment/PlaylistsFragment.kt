package com.example.playlistmaker.ui.media.playlists.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.favorites.playlists.model.Playlist
import com.example.playlistmaker.ui.media.playlists.PlaylistGridAdapter
import com.example.playlistmaker.ui.media.playlists.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val playlists = ArrayList<Playlist>()
    private val adapter: PlaylistGridAdapter = PlaylistGridAdapter()

    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        adapter.onClickedPlaylist = { playlist ->
            val bundle = Bundle().apply {
                putSerializable("selected_playlist", playlist)
            }
            findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment, bundle)
        }
        buildRV()
    }

    private fun buildRV() {
        adapter.playlists = playlists
        binding.playlistRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRV.adapter = adapter
    }

    private fun render(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            binding.placeholderLL.visibility = View.VISIBLE
            binding.playlistRV.visibility = View.GONE
        } else {
            binding.placeholderLL.visibility = View.GONE
            binding.playlistRV.visibility = View.VISIBLE
            adapter.playlists.clear()
            adapter.playlists.addAll(playlists)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}