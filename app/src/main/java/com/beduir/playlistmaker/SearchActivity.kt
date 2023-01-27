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

        val backButton = findViewById<ImageView>(R.id.back_button)
        inputSearchText = findViewById<EditText>(R.id.search_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)

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
}