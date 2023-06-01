package com.beduir.playlistmaker.main.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.media.presentation.MediaActivity
import com.beduir.playlistmaker.search.presentation.SearchActivity
import com.beduir.playlistmaker.settings.presentation.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(
            this, MainViewModel.getViewModelFactory(
            )
        )[MainViewModel::class.java]

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        searchButton.setOnClickListener { viewModel.onSearchButtonClicked() }
        mediaButton.setOnClickListener { viewModel.onMediaButtonClicked() }
        settingsButton.setOnClickListener { viewModel.onSettingsButtonClicked() }

        viewModel.observeMenu().observe(this) {
            navigate(it)
        }
    }

    private fun navigate(item: MainMenuItems) {
        when (item) {
            MainMenuItems.Search -> startActivity(Intent(this, SearchActivity::class.java))
            MainMenuItems.Media -> startActivity(Intent(this, MediaActivity::class.java))
            MainMenuItems.Settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}