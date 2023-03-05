package com.beduir.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    companion object {
        const private val SEARCH_TEXT = "SEARCH_TEXT"
        const private val SEARCH_TEXT_POSITION = "SEARCH_TEXT_POSITION"
        const private val SEARCH_STATE = "SEARCH_STATE"
        const private val I_TUNES_API_URL = "https://itunes.apple.com"
        const private val SEARCH_PREFERENCES = "search"
        const private val SEARCH_HISTORY = "search_history"
    }

    // Реализуем сохранение, как требуется в ТЗ. Хотя для TextEdit оно и так само работает.
    private lateinit var inputSearchText: EditText
    private lateinit var searchResult: RecyclerView
    private lateinit var historyList: RecyclerView
    private lateinit var placeholderNothingWasFound: ScrollView
    private lateinit var placeholderNetworkIssues: ScrollView
    private lateinit var history: LinearLayout

    private val retrofit = Retrofit.Builder()
        .baseUrl(I_TUNES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesAPI::class.java)

    private val trackAdapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private var searchState: SearchStates = SearchStates.SEARCH_RESULT
    private var searchText: String? = "" // Тоже требуется по ТЗ :/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.back_button)
        inputSearchText = findViewById<EditText>(R.id.search_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        searchResult = findViewById<RecyclerView>(R.id.search_result)
        historyList = findViewById<RecyclerView>(R.id.history_list)
        val refreshButton = findViewById<Button>(R.id.refresh)
        placeholderNothingWasFound = findViewById<ScrollView>(R.id.placeholder_nothing_was_found)
        placeholderNetworkIssues = findViewById<ScrollView>(R.id.placeholder_network_issues)
        history = findViewById<LinearLayout>(R.id.history)
        val clearHistory = findViewById<Button>(R.id.clear_history)

        searchResult.adapter = trackAdapter
        historyList.adapter = historyAdapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT, "")
            inputSearchText.setText(searchText)
            inputSearchText.setSelection(
                savedInstanceState.getInt(
                    SEARCH_TEXT_POSITION, 0
                )
            )
            setSearchState(
                SearchStates.getByValue(
                    savedInstanceState.getInt(
                        SEARCH_STATE,
                        SearchStates.SEARCH_RESULT.value
                    )
                )!!
            )
        }
        readTrackHistory()

        clearButton.setOnClickListener {
            if (searchResult.visibility == View.GONE) {
                setSearchState(SearchStates.SEARCH_RESULT)
            } else {
                trackAdapter.tracks.clear()
                trackAdapter.notifyDataSetChanged()
            }
            inputSearchText.setText("")
            inputSearchText.clearFocus()
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = inputSearchText?.toString() ?: ""
                if (inputSearchText.hasFocus() && s?.isEmpty() == true
                    && historyAdapter.tracks.size > 0) {
                    showHistory()
                }
                else {
                    setSearchState(searchState)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputSearchText.addTextChangedListener(simpleTextWatcher)

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputSearchText.text.toString())
            }
            false
        }

        inputSearchText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputSearchText.text.isEmpty() && historyAdapter.tracks.size > 0) {
                showHistory()
            }
            else {
                setSearchState(searchState)
            }
        }

        refreshButton.setOnClickListener {
            search(inputSearchText.text.toString())
        }

        backButton.setOnClickListener {
            finish()
        }

        clearHistory.setOnClickListener {
            clearTrackHistory()
        }

        trackAdapter.itemClickListener = { track ->
            addTrackToHistory(track)
            openPlayer(track)
        }

        historyAdapter.itemClickListener = { track ->
            addTrackToHistory(track)
            openPlayer(track)
        }
    }

    private fun openPlayer(track: Track) {
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(PlayerActivity.TRACK_VALUE, track)
        startActivity(playerIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putString(SEARCH_TEXT, searchText)
            putInt(SEARCH_TEXT_POSITION, inputSearchText.selectionStart)
            putInt(SEARCH_STATE, searchState.value)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search(text: String) {
        setSearchState(SearchStates.SEARCH_RESULT)
        history.visibility = View.GONE
        iTunesService
            .search(text)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        trackAdapter.tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackAdapter.tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (trackAdapter.tracks.isEmpty()) {
                            setSearchState(SearchStates.NOTHING_WAS_FOUND)
                        }
                    } else {
                        setSearchState(SearchStates.NETWORK_ISSUES)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    setSearchState(SearchStates.NETWORK_ISSUES)
                }

            })
    }

    private fun setSearchState(state: SearchStates) {
        history.visibility = View.GONE
        searchResult.visibility =
            if (state == SearchStates.SEARCH_RESULT) View.VISIBLE else View.GONE
        when (state) {
            SearchStates.NETWORK_ISSUES -> placeholderNetworkIssues.visibility = View.VISIBLE
            SearchStates.NOTHING_WAS_FOUND -> placeholderNothingWasFound.visibility = View.VISIBLE
            else -> {
                placeholderNothingWasFound.visibility = View.GONE
                placeholderNetworkIssues.visibility = View.GONE
            }
        }
        searchState = state
    }

    private fun showHistory() {
        placeholderNothingWasFound.visibility = View.GONE
        placeholderNetworkIssues.visibility = View.GONE
        searchResult.visibility = View.GONE
        history.visibility = View.VISIBLE
    }

    private fun addTrackToHistory(track: Track) {
        val sharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        val index = historyAdapter.tracks.indexOfFirst { it.trackId == track.trackId }
        if (index == 0) return
        if (index != -1) {
            historyAdapter.tracks.removeAt(index)
            historyAdapter.notifyItemRemoved(index)
            historyAdapter.notifyItemRangeChanged(index, historyAdapter.tracks.size - index)
        }
        if (historyAdapter.tracks.size == 10) {
            historyAdapter.tracks.removeLast()
            historyAdapter.notifyItemRemoved(10)
            historyAdapter.notifyItemRangeChanged(9, 1)
        }
        historyAdapter.tracks.add(0, track)
        historyAdapter.notifyItemInserted(0)
        historyAdapter.notifyItemRangeChanged(0, historyAdapter.tracks.size - 1)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY, createJsonFromTracksList(historyAdapter.tracks))
            .apply()
    }

    private fun readTrackHistory() {
        val sharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        val tracksJson = sharedPrefs.getString(SEARCH_HISTORY, null)
        if (tracksJson != null) {
            historyAdapter.tracks.addAll(createTracksListFromJson(tracksJson))
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun clearTrackHistory() {
        val sharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY)
            .apply()
        historyAdapter.tracks.clear()
        historyAdapter.notifyDataSetChanged()
        setSearchState(searchState)
    }

    private fun createTracksListFromJson(json: String): ArrayList<Track> {
        val trackType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, trackType)
    }

    private fun createJsonFromTracksList(facts: ArrayList<Track>): String {
        return Gson().toJson(facts)
    }
}