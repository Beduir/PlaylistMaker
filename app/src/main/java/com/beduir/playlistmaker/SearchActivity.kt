package com.beduir.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    // Реализуем сохранение, как требуется в ТЗ. Хотя для TextEdit оно и так само работает.
    private lateinit var inputSearchText: EditText

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_POSITION = "SEARCH_TEXT_POSITION"
        var searchText: String? = "" // Тоже требуется по ТЗ :/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val tracksList: List<Track> = getTracksList()

        val backButton = findViewById<ImageView>(R.id.back_button)
        inputSearchText = findViewById<EditText>(R.id.search_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        val searchResult = findViewById<RecyclerView>(R.id.search_result)

        val trackAdapter = TrackAdapter(tracksList)
        searchResult.adapter = trackAdapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT, "")
            inputSearchText.setText(searchText)
            inputSearchText.setSelection(savedInstanceState.getInt(
                SEARCH_TEXT_POSITION, 0))
        }

        clearButton.setOnClickListener {
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

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putString(SEARCH_TEXT, searchText)
            putInt(SEARCH_TEXT_POSITION, inputSearchText.selectionStart)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun getTracksList(): List<Track> {
        return listOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
            ),
        )
    }
}