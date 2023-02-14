package com.beduir.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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
    }

    // Реализуем сохранение, как требуется в ТЗ. Хотя для TextEdit оно и так само работает.
    private lateinit var inputSearchText: EditText
    private lateinit var searchResult: RecyclerView
    private lateinit var placeholderNothingWasFound: ScrollView
    private lateinit var placeholderNetworkIssues: ScrollView

    private val retrofit = Retrofit.Builder()
        .baseUrl(I_TUNES_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesAPI::class.java)

    private val trackAdapter = TrackAdapter()

    private var searchState: SearchStates = SearchStates.SEARCH_RESULT
    private var searchText: String? = "" // Тоже требуется по ТЗ :/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.back_button)
        inputSearchText = findViewById<EditText>(R.id.search_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        searchResult = findViewById<RecyclerView>(R.id.search_result)
        val refreshButton = findViewById<Button>(R.id.refresh)
        placeholderNothingWasFound = findViewById<ScrollView>(R.id.placeholder_nothing_was_found)
        placeholderNetworkIssues = findViewById<ScrollView>(R.id.placeholder_network_issues)

        searchResult.adapter = trackAdapter

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

        refreshButton.setOnClickListener {
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
        if (searchState == state) {
            return
        }
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
}