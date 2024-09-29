package com.example.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var interactor: TrackInteractor
    private lateinit var historyInteractor: SearchHistoryInteractor
    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var searchRunnable: Runnable
    private lateinit var progressBar: ProgressBar
    private lateinit var historyDefaultConsumer: SearchHistoryInteractor.SearchHistoryConsumer

    private var searchText: String? = null
    private lateinit var placeholderLL: LinearLayout
    private lateinit var placeholderTextView: TextView
    private lateinit var placeholderImageView: ImageView
    private lateinit var searchHistoryLL: ScrollView
    private lateinit var searchEditText: EditText
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private val historyAdapter: TrackAdapter = TrackAdapter()
    private var isClickAllowed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        interactor = Creator.provideTrackInteractor()
        historyInteractor = Creator.provideHistoryInteractor()
        searchRunnable = Runnable { search() }
        historyDefaultConsumer = object: SearchHistoryInteractor.SearchHistoryConsumer{
            override fun consume(historyTracks: ArrayList<Track>) {
                historyAdapter.tracks = historyTracks
                historyAdapter.notifyDataSetChanged()
            }

            override fun onError(errorMessage: String) {
                Log.e("ERROR", errorMessage)
            }
        }

        // EditText for searching songs
        searchEditText = findViewById(R.id.searchEditText)
        searchHistoryLL = findViewById(R.id.searchHistoryLL)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        val clearHistoryButton = findViewById<Button>(R.id.clearHistory)
        showHistory()

        adapter.onClickedTrack = {track ->
            handleTrackClick(track)
        }

        historyAdapter.onClickedTrack = {track ->
            handleTrackClick(track)
        }

        // clearHistory button
        clearHistoryButton.setOnClickListener {
            historyInteractor.clearSearchHistory()
            updateHistoryRV()
            placeholderLL.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }

        // clear button IN editText
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            it.visibility = View.GONE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderLL.visibility = View.GONE
            showHistory()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        searchEditText.addTextChangedListener( // listener for text changing
            onTextChanged = { text, _, _, _ ->
                if (!text.isNullOrEmpty()) clearButton.visibility = View.VISIBLE
                else clearButton.visibility = View.INVISIBLE
                tracks.clear()
                adapter.notifyDataSetChanged()
                if (!text.isNullOrEmpty()) {
                    searchText = text.toString()
                    searchDebounce()
                }
                placeholderLL.visibility = View.GONE
                searchHistoryLL.visibility = if (searchEditText.hasFocus() && text?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        )

        searchEditText.setOnFocusChangeListener { _, hasFocus -> // listener for focus changing(just for searchHistory)
            searchHistoryLL.visibility = if (hasFocus && searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }


        // placeholder(nothing found or bad internet)
        placeholderLL = findViewById(R.id.placeholderLL)
        placeholderLL.visibility = View.GONE
        placeholderImageView = findViewById(R.id.placeholderIV)
        placeholderTextView = findViewById(R.id.placeholderTextView)
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            search()
        }



        progressBar = findViewById(R.id.progressBar)

        clickDebounce()
        buildRV()
    }

    private fun buildRV(){
        recyclerView = findViewById(R.id.searchRecyclerView)
        adapter.tracks = tracks
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val searchHistoryRV = findViewById<RecyclerView>(R.id.searchHistoryRV)
        historyInteractor.getHistory(historyDefaultConsumer)
        searchHistoryRV.adapter = historyAdapter
        searchHistoryRV.layoutManager = LinearLayoutManager(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        findViewById<EditText>(R.id.searchEditText).setText(savedInstanceState.getString(SEARCH_TEXT))
        placeholderLL.visibility = View.GONE
    }

    private fun search() {

        placeholderLL.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        tracks.clear()
        adapter.notifyDataSetChanged()

        searchText?.let {

            interactor.searchTracks(it, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: ArrayList<Track>) {
                    searchText = null
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        if (foundTracks.isNotEmpty()) {
                            showPlaceholder(null, null)
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                            recyclerView.visibility = View.VISIBLE
                        } else showPlaceholder(R.string.nothing_found, R.drawable.nothing_found)
                    }
                }

                override fun onError(errorMessage: String) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        showPlaceholder(R.string.net_problems, R.drawable.no_internet)
                    }
                }
            })
        }
    }


    private fun showPlaceholder(@StringRes message: Int?, @DrawableRes icon: Int?){
        if (message != null && icon != null){
            if (message == R.string.net_problems) refreshButton.visibility = View.VISIBLE
            else refreshButton.visibility = View.GONE
            placeholderLL.visibility = View.VISIBLE
            placeholderImageView.setImageResource(icon)
            placeholderTextView.setText(message)
        }
        else{
            placeholderLL.visibility = View.GONE
        }
    }

    private fun handleTrackClick(track: Track){
        historyInteractor.saveHistory(track)
        updateHistoryRV()

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("selected_track", track)
        startActivity(intent)
    }

    private fun updateHistoryRV(){
        historyInteractor.getHistory(historyDefaultConsumer)
        val isHistoryEmpty = historyAdapter.tracks.isEmpty()

        searchHistoryLL.visibility = if (isHistoryEmpty || searchEditText.hasFocus())
            View.GONE else View.VISIBLE
    }


    private fun showHistory(){
        if (historyAdapter.tracks.isNotEmpty()) searchHistoryLL.visibility = View.VISIBLE
        else searchHistoryLL.visibility = View.GONE
    }

    private fun searchDebounce(){
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}