package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.model.TracksState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private var searchText: String = ""
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private val historyAdapter: TrackAdapter = TrackAdapter()
    private var isClickAllowed = true

    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        viewModel.observeSearchState().observe(this){
            render(it)
        }
        viewModel.observeHistoryChanged().observe(this){
            if (it) {
                historyAdapter.tracks = viewModel.getHistoryTracks()
                historyAdapter.notifyDataSetChanged()
            }
        }


        adapter.onClickedTrack = {track ->
            handleTrackClick(track)
        }

        historyAdapter.onClickedTrack = {track ->
            handleTrackClick(track)
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
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }

        binding.searchEditText.addTextChangedListener( // listener for text changing
            onTextChanged = { text, _, _, _ ->
                adapter.tracks.clear()
                adapter.notifyDataSetChanged()
                if (text != null) {
                    binding.clearButton.visibility = View.VISIBLE
                    searchText = text.toString()
                    viewModel.searchDebounce(searchText)
                }
                else {
                    binding.clearButton.visibility = View.INVISIBLE
                }

                binding.searchHistoryLL.visibility = if (binding.searchEditText.hasFocus() && text?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus -> // listener for focus changing(just for searchHistory)
            binding.searchHistoryLL.visibility = if (hasFocus && binding.searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }


        // placeholder(nothing found or bad internet)
        binding.placeholderLL.visibility = View.GONE
        binding.refreshButton.setOnClickListener {
            if (searchText != "")
                viewModel.searchDebounce(searchText)
        }

        clickDebounce()
        buildRV()
    }

    private fun render(state: TracksState){
        if (state !is TracksState.Loading) searchText = ""
        when (state){
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty()
            is TracksState.Error -> showError()
            is TracksState.Loading -> showLoading()
        }
    }

    private fun showLoading(){
        binding.placeholderLL.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError(){
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        showPlaceholder(R.string.net_problems, R.drawable.no_internet)
    }

    private fun showEmpty(){
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        showPlaceholder(R.string.nothing_found, R.drawable.nothing_found)
    }

    private fun showContent(tracks: ArrayList<Track>){
        binding.progressBar.visibility = View.GONE
        binding.placeholderLL.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE
        showPlaceholder(null, null)

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun buildRV(){
        adapter.tracks = tracks
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.adapter = adapter


        binding.searchHistoryRV.adapter = historyAdapter
        historyAdapter.tracks = viewModel.getHistoryTracks()
        binding.searchHistoryRV.layoutManager = LinearLayoutManager(this)
        binding.searchHistoryLL.visibility = if (!binding.searchEditText.hasFocus() || historyAdapter.tracks.isEmpty())
            View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchEditText.setText(savedInstanceState.getString(SEARCH_TEXT))
        binding.placeholderLL.visibility = View.GONE
    }


    private fun showPlaceholder(@StringRes message: Int?, @DrawableRes icon: Int?){
        if (message != null && icon != null){
            if (message == R.string.net_problems) binding.refreshButton.visibility = View.VISIBLE
            else binding.refreshButton.visibility = View.GONE
            binding.placeholderLL.visibility = View.VISIBLE
            binding.placeholderIV.setImageResource(icon)
            binding.placeholderTextView.setText(message)
        }
        else{
            binding.placeholderLL.visibility = View.GONE
        }
    }

    private fun handleTrackClick(track: Track){
        viewModel.saveHistory(track)

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("selected_track", track)
        startActivity(intent)
    }

    private fun clickDebounce() : Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    companion object{
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}