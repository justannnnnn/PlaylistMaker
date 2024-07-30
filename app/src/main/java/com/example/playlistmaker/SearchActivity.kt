package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = TEXT_DEF

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(ITunesAPI::class.java)

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private lateinit var placeholderLL: LinearLayout
    private lateinit var placeholderTextView: TextView
    private lateinit var placeholderImageView: ImageView
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView

    private val historyAdapter: TrackAdapter = TrackAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(App.PLAYLIST_PREFERENCES, MODE_PRIVATE)


        // Back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // EditText for searching songs
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchHistoryLL = findViewById<ScrollView>(R.id.searchHistoryLL)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        searchEditText.addTextChangedListener( // listener for text changing
            onTextChanged = { text, _, _, _ ->
                if (!text.isNullOrEmpty()) clearButton.visibility = View.VISIBLE
                else clearButton.visibility = View.INVISIBLE

                searchText = text.toString()

                // new track list for new search query
                tracks.clear()
                adapter.notifyDataSetChanged()

                // visibility of placeholder and search history
                placeholderLL.visibility = View.GONE
                searchHistoryLL.visibility = if (searchEditText.hasFocus() && text?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        )
        searchEditText.setOnEditorActionListener { _, actionId, _ -> // listener for DONE button on keyboard
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (searchText.isNotEmpty()) search()
                true
            }
            false
        }
        searchEditText.setOnFocusChangeListener { v, hasFocus -> // listener for focus changing(just for searchHistory)
            searchHistoryLL.visibility = if (hasFocus && searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }
        // clear button IN editText
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderLL.visibility = View.GONE
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        // search History
        val searchHistoryRV = findViewById<RecyclerView>(R.id.searchHistoryRV)
        val clearHistoryButton = findViewById<Button>(R.id.clearHistory)

        //val historyAdapter = TrackAdapter()
        val searchHistory = SearchHistory(sharedPreferences, historyAdapter) // instance of class searchHistory for working with historyTracks
        historyAdapter.tracks = searchHistory.getTracks()
        searchHistoryRV.adapter = historyAdapter
        searchHistoryRV.layoutManager = LinearLayoutManager(this)

        // clearHistory button
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
            searchHistoryLL.visibility = View.GONE
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

        recyclerView = findViewById(R.id.searchRecyclerView)
        adapter.tracks = tracks
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        findViewById<EditText>(R.id.searchEditText).setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    private fun search(){
        tracksService.searchSongs(searchText)
            .enqueue(object : Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>
                ) {
                    if (response.code() == 200){
                        if (response.body()?.results?.isNotEmpty() == true){
                            showPlaceholder(null, null)
                            tracks.clear()
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        else {
                            showPlaceholder(R.string.nothing_found, R.drawable.nothing_found)
                        }
                    }
                    else showPlaceholder(R.string.net_problems, R.drawable.no_internet)
                }

                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                    showPlaceholder(R.string.net_problems, R.drawable.no_internet)
                }

            })
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


    companion object{
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""

    }

}