package com.beduir.playlistmaker.search.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.search.domain.Track
import com.beduir.playlistmaker.TrackAdapter
import com.beduir.playlistmaker.iTunesAPI
import com.beduir.playlistmaker.search.data.SearchRepository
import com.beduir.playlistmaker.search.data.TrackHistory
import com.beduir.playlistmaker.search.domain.SearchInteractor
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), SearchScreenView {
    companion object {
        private const val SEARCH_STATE = "SEARCH_STATE"
        private const val I_TUNES_API_URL = "https://itunes.apple.com"
        private const val SEARCH_PREFERENCES = "search"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var inputSearchText: EditText
    private lateinit var searchResult: RecyclerView
    private lateinit var historyList: RecyclerView
    private lateinit var placeholderNothingWasFound: View
    private lateinit var placeholderNetworkIssues: View
    private lateinit var history: View
    private lateinit var progressBar: View
    private lateinit var backButton: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var clearHistory: Button

    private lateinit var trackHistory: TrackHistory
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    private val retrofit = Retrofit.Builder()
        .baseUrl(I_TUNES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val repository = SearchRepository(retrofit.create(iTunesAPI::class.java))

    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var searchState: SearchStates = SearchStates.SEARCH_RESULT
    private val searchRunnable = Runnable { loadTracks() }

    private var isTrackClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var presenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initHistory()
        initAdapter()

        presenter = SearchPresenter(
            view = this,
            interactor = SearchInteractor(
                trackHistory = trackHistory,
                repository = repository
            ),
            router = SearchRouter(this)
        )

        if (savedInstanceState != null) {
            setSearchState(
                SearchStates.getByValue(
                    savedInstanceState.getInt(
                        SEARCH_STATE,
                        SearchStates.SEARCH_RESULT.value
                    )
                )!!
            )
        }

        clearButton.setOnClickListener {
            presenter.searchTextClearClicked()
        }

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
                presenter.searchTextChanged(
                    hasFocus = inputSearchText.hasFocus(),
                    text = text,
                    onSearch = { searchDebounce() }
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        inputSearchText.setOnFocusChangeListener { _, hasFocus ->
            presenter.searchFocusChanged(hasFocus, inputSearchText.text.toString())
        }

        refreshButton.setOnClickListener {
            loadTracks()
        }

        backButton.setOnClickListener {
            presenter.backButtonClicked()
        }

        clearHistory.setOnClickListener {
            presenter.onHistoryClearClicked()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(SEARCH_STATE, searchState.value)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showHistory(tracks: ArrayList<Track>) {
        setSearchState(SearchStates.HISTORY)
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    override fun showEmptyResult() {
        setSearchState(SearchStates.NOTHING_WAS_FOUND)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showTracks(tracks: List<Track>) {
        setSearchState(SearchStates.SEARCH_RESULT)
        historyAdapter.tracks.clear()
        historyAdapter.notifyDataSetChanged()
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    override fun showSearchError() {
        setSearchState(SearchStates.NETWORK_ISSUES)
    }

    override fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
    }

    override fun clearSearchText() {
        inputSearchText.setText("")
        inputSearchText.clearFocus()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showLoading() {
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        history.visibility = View.GONE
        searchResult.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        inputSearchText = findViewById(R.id.search_text)
        clearButton = findViewById(R.id.clear_icon)
        searchResult = findViewById(R.id.search_result)
        historyList = findViewById(R.id.history_list)
        refreshButton = findViewById(R.id.refresh)
        placeholderNothingWasFound = findViewById(R.id.placeholder_nothing_was_found)
        placeholderNetworkIssues = findViewById(R.id.placeholder_network_issues)
        history = findViewById(R.id.history)
        progressBar = findViewById(R.id.progress_bar)
        clearHistory = findViewById(R.id.clear_history)
    }

    private fun initHistory() {
        sharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        trackHistory = TrackHistory(sharedPrefs, gson)
    }

    private fun initAdapter() {
        searchResult.adapter = trackAdapter
        historyList.adapter = historyAdapter
        trackAdapter.itemClickListener = { onTrackClick(it) }
        historyAdapter.itemClickListener = { onTrackClick(it) }
    }

    private fun trackClickDebounce() : Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onTrackClick(track: Track) {
        if (trackClickDebounce()) {
            presenter.onHistoryTrackClicked(track)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun loadTracks() {
        presenter.loadTracks(inputSearchText.text.toString())
    }

    private fun setSearchState(state: SearchStates) {
        progressBar.visibility = View.GONE
        when (state) {
            SearchStates.NETWORK_ISSUES -> {
                searchResult.visibility = View.GONE
                history.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderNetworkIssues.visibility = View.VISIBLE
            }
            SearchStates.NOTHING_WAS_FOUND -> {
                searchResult.visibility = View.GONE
                history.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.VISIBLE
                placeholderNetworkIssues.visibility = View.GONE
            }
            SearchStates.HISTORY -> {
                placeholderNothingWasFound.visibility = View.GONE
                placeholderNetworkIssues.visibility = View.GONE
                searchResult.visibility = View.GONE
                history.visibility = View.VISIBLE
            }
            SearchStates.SEARCH_RESULT -> {
                placeholderNothingWasFound.visibility = View.GONE
                placeholderNetworkIssues.visibility = View.GONE
                searchResult.visibility = View.VISIBLE
                history.visibility = View.GONE
            }
        }
        searchState = state
    }
}