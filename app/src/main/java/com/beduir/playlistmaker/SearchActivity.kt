package com.beduir.playlistmaker

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_POSITION = "SEARCH_TEXT_POSITION"
        const val SEARCH_STATE = "SEARCH_STATE"
        var searchText: String? = "" // Тоже требуется по ТЗ :/
    }

    enum class SearchStates(val value: Int) {
        SEARCH_RESULT(1),
        NETWORK_ISSUES(2),
        NOTHING_WAS_FOUND(3);

        companion object {
            private val VALUES = values()
            fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
        }
    }

    // Реализуем сохранение, как требуется в ТЗ. Хотя для TextEdit оно и так само работает.
    private lateinit var inputSearchText: EditText
    private lateinit var searchResult: RecyclerView
    private lateinit var placeholderNothingWasFound: LinearLayout
    private lateinit var placeholderNetworkIssues: LinearLayout

    private val iTunesAPIUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesAPIUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesAPI::class.java)

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter()

    private var searchState: SearchStates = SearchStates.SEARCH_RESULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.back_button)
        inputSearchText = findViewById<EditText>(R.id.search_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        searchResult = findViewById<RecyclerView>(R.id.search_result)
        val refreshButton = findViewById<Button>(R.id.refresh)
        placeholderNothingWasFound = findViewById<LinearLayout>(R.id.placeholder_nothing_was_found)
        placeholderNetworkIssues = findViewById<LinearLayout>(R.id.placeholder_network_issues)

        trackAdapter.tracks = tracks
        searchResult.adapter = trackAdapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT, "")
            inputSearchText.setText(searchText)
            inputSearchText.setSelection(savedInstanceState.getInt(
                SEARCH_TEXT_POSITION, 0))
            setSearchState(SearchStates.getByValue(savedInstanceState.getInt(SEARCH_STATE,
                SearchStates.SEARCH_RESULT.value))!!)
        }

        clearButton.setOnClickListener {
            if (searchResult.visibility == View.GONE) {
                setSearchState(SearchStates.SEARCH_RESULT)
            }
            else {
                tracks.clear()
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
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputSearchText.addTextChangedListener(simpleTextWatcher)

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputSearchText.text.toString())
                true
            }
            false
        }

        refreshButton.setOnClickListener{
            search(inputSearchText.text.toString())
        }

        backButton.setOnClickListener {
            finish()
        }
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
        iTunesService
            .search(text)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
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
        if (searchState == state) {
            return
        }
        searchResult.visibility = if (state == SearchStates.SEARCH_RESULT) View.VISIBLE else View.GONE
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
}