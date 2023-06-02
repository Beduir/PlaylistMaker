package com.beduir.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beduir.playlistmaker.player.domain.PlayerInteractor
import com.beduir.playlistmaker.search.domain.models.Track

class PlayerViewModelFactory(
    private val track: Track,
    private val interactor: PlayerInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(track, interactor) as T
    }
}