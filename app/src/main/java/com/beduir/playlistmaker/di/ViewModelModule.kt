package com.beduir.playlistmaker.di

import com.beduir.playlistmaker.player.presentation.PlayerViewModel
import com.beduir.playlistmaker.search.domain.models.Track
import com.beduir.playlistmaker.search.presentation.SearchViewModel
import com.beduir.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (track: Track) ->
        PlayerViewModel(track, get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get())
    }
}