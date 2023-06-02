package com.beduir.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beduir.playlistmaker.search.domain.SearchInteractor

class SearchViewModelFactory(
    private val interactor: SearchInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(interactor) as T
    }
}