package com.example.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.model.TracksState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var textForSearch: String = ""
    private var lastTextSearch: String = ""
    private val tracks = ArrayList<Track>()
    private val adapter: TrackAdapter = TrackAdapter()
    private val historyAdapter: TrackAdapter = TrackAdapter()
    private var isClickAllowed = true

    private lateinit var binding: FragmentSearchBinding
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeHistoryChanged().observe(viewLifecycleOwner) {
            if (it) {
                historyAdapter.tracks = viewModel.getHistoryTracks()
                historyAdapter.notifyDataSetChanged()
            }
        }

        onTrackClickDebounce = { track ->
            if (clickDebounce()) {
                viewModel.saveHistory(track)
                val bundle = Bundle().apply {
                    putSerializable("selected_track", track)
                }
                findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
            }
        }

        adapter.onClickedTrack = { track ->
            onTrackClickDebounce(track)
        }

        historyAdapter.onClickedTrack = { track ->
            onTrackClickDebounce(track)
        }

        // clearHistory button
        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.searchHistoryLL.visibility = View.GONE
        }


        // clear button IN editText
        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            it.visibility = View.GONE
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.placeholderLL.visibility = View.GONE
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                binding.searchEditText.windowToken,
                0
            )
        }

        binding.searchEditText.addTextChangedListener( // listener for text changing
            onTextChanged = { text, _, _, _ ->
                adapter.tracks.clear()
                adapter.notifyDataSetChanged()
                if (!text.isNullOrEmpty()) {
                    binding.clearButton.visibility = View.VISIBLE
                    textForSearch = text.toString()
                    lastTextSearch = textForSearch
                    viewModel.searchDebounce(textForSearch)
                } else {
                    binding.clearButton.visibility = View.INVISIBLE
                    lastTextSearch = ""
                    viewModel.searchDebounce("")
                }

                binding.searchHistoryLL.visibility =
                    if (binding.searchEditText.hasFocus() && text?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus -> // listener for focus changing(just for searchHistory)
            binding.searchHistoryLL.visibility =
                if (hasFocus && binding.searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }


        // placeholder(nothing found or bad internet)
        binding.placeholderLL.visibility = View.GONE
        binding.refreshButton.setOnClickListener {
            if (textForSearch != "")
                viewModel.searchDebounce(textForSearch)
        }

        clickDebounce()
        buildRV()
    }

    private fun render(state: TracksState) {
        if (state !is TracksState.Loading && state !is TracksState.Error) textForSearch = ""
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty()
            is TracksState.Error -> showError()
            is TracksState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.placeholderLL.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        showPlaceholder(R.string.net_problems, R.drawable.no_internet)
    }

    private fun showEmpty() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        showPlaceholder(R.string.nothing_found, R.drawable.nothing_found)
    }

    private fun showContent(tracks: ArrayList<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderLL.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE
        showPlaceholder(null, null)

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun buildRV() {
        adapter.tracks = tracks
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter = adapter


        binding.searchHistoryRV.adapter = historyAdapter
        historyAdapter.tracks = viewModel.getHistoryTracks()
        binding.searchHistoryRV.layoutManager = LinearLayoutManager(requireContext())
        binding.searchHistoryLL.visibility =
            if (!binding.searchEditText.hasFocus() || historyAdapter.tracks.isEmpty())
                View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, lastTextSearch)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT))
        }
        binding.placeholderLL.visibility = View.GONE
    }


    private fun showPlaceholder(@StringRes message: Int?, @DrawableRes icon: Int?) {
        if (message != null && icon != null) {
            if (message == R.string.net_problems) binding.refreshButton.visibility = View.VISIBLE
            else binding.refreshButton.visibility = View.GONE
            binding.placeholderLL.visibility = View.VISIBLE
            binding.placeholderIV.setImageResource(icon)
            binding.placeholderTextView.setText(message)
        } else {
            binding.placeholderLL.visibility = View.GONE
        }
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


}