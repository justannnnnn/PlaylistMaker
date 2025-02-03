package com.example.playlistmaker.ui.media.favorites.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.favorites.model.FavoritesState
import com.example.playlistmaker.ui.media.favorites.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var isClickAllowed = true
    private val tracks = ArrayList<Track>()
    private val adapter: TrackAdapter = TrackAdapter()

    private val viewModel: FavoritesViewModel by viewModel<FavoritesViewModel>()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getFavoritesData()
        viewModel.observeFavoritesState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = { track ->
            if (clickDebounce()) {
                val bundle = Bundle().apply {
                    putSerializable("selected_track", track)
                }
                findNavController().navigate(R.id.action_mediaFragment_to_playerFragment, bundle)
            }
        }
        adapter.onClickedTrack = { track ->
            onTrackClickDebounce(track)
        }

        clickDebounce()
        buildRV()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoritesData()
    }

    private fun buildRV() {
        adapter.tracks = tracks
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
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

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            FavoritesState.IsEmpty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.placeholderLL.visibility = View.GONE
        binding.favoritesRecyclerView.visibility = View.VISIBLE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.placeholderLL.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}