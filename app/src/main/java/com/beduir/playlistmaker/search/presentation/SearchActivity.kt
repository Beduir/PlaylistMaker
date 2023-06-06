package com.beduir.playlistmaker.search.presentation

import android.annotation.SuppressLint
import android.content.Context
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
import com.beduir.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_PREFERENCES = "search"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel: SearchViewModel by viewModel()
    private val router: SearchRouter by lazy { SearchRouter(this) }

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

    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var isTrackClickAllowed = true

    private var textWatcher: TextWatcher? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initAdapter()

        clearButton.setOnClickListener {
            viewModel.clearSearchText()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = text?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { inputSearchText.addTextChangedListener(it) }

        inputSearchText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.searchTextChangeFocus(hasFocus, inputSearchText.text.toString())
        }

        refreshButton.setOnClickListener {
            viewModel.refreshSearch(inputSearchText.text.toString())
        }

        backButton.setOnClickListener {
            router.goBack()
        }

        clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeStateSearchField().observe(this) {
            updateSearchTextField(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputSearchText.removeTextChangedListener(it) }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
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

    private fun initAdapter() {
        searchResult.adapter = trackAdapter
        historyList.adapter = historyAdapter
        trackAdapter.itemClickListener = { onTrackClick(it) }
        historyAdapter.itemClickListener = { onTrackClick(it) }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Tracks -> showTracks(state.tracks)
            is SearchState.History -> showHistory(state.tracks)
            is SearchState.Loading -> showLoading()
            is SearchState.NetworkIssue -> showNetworkIssue()
            is SearchState.NothingWasFound -> showNothingWasFound()
        }
    }

    private fun updateSearchTextField(state: SearchFieldState) {
        when (state) {
            is SearchFieldState.SearchTextEmpty -> {
                clearButton.visibility = View.GONE
                if (state.needClear) {
                    inputSearchText.setText("")
                    inputSearchText.clearFocus()
                    hideKeyboard()
                }
            }

            SearchFieldState.SearchTextEntered -> {
                clearButton.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showTracks(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.GONE
        searchResult.visibility = View.VISIBLE
        history.visibility = View.GONE

        historyAdapter.tracks.clear()
        historyAdapter.notifyDataSetChanged()
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.GONE
        searchResult.visibility = View.GONE
        history.visibility = View.VISIBLE

        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showLoading() {
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        progressBar.visibility = View.VISIBLE
        history.visibility = View.GONE
        searchResult.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.GONE
    }

    private fun showNetworkIssue() {
        progressBar.visibility = View.GONE
        searchResult.visibility = View.GONE
        history.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.VISIBLE
    }

    private fun showNothingWasFound() {
        progressBar.visibility = View.GONE
        searchResult.visibility = View.GONE
        history.visibility = View.GONE
        placeholderNothingWasFound.visibility = View.VISIBLE
        placeholderNetworkIssues.visibility = View.GONE
    }

    private fun trackClickDebounce(): Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        if (trackClickDebounce()) {
            viewModel.openTrack(track)
            router.openTrack(track)
        }
    }
}