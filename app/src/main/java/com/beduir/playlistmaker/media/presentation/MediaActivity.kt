package com.beduir.playlistmaker.media.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beduir.playlistmaker.R

class MediaActivity : AppCompatActivity() {
    private lateinit var viewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        viewModel = ViewModelProvider(
            this, MediaViewModel.getViewModelFactory(
            )
        )[MediaViewModel::class.java]
    }
}