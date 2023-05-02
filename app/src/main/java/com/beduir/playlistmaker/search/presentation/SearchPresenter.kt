package com.beduir.playlistmaker.search.presentation

import com.beduir.playlistmaker.search.domain.ISearchInteractor
import com.beduir.playlistmaker.search.domain.Track

class SearchPresenter(
    private val view: SearchScreenView,
    private val interactor: ISearchInteractor,
    private val router: SearchRouter
) {
    fun onHistoryClearClicked() {
        interactor.clearHistory()
        view.showTracks(emptyList())
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        val historyTracks = interactor.getHistory()
        if (hasFocus && text.isEmpty() && historyTracks.isNotEmpty()) {
            view.showHistory(historyTracks)
        }
        else {
            view.showTracks(emptyList())
        }
    }

    fun searchTextChanged(hasFocus: Boolean, text: CharSequence?, onSearch: () -> Unit) {
        if (hasFocus && text?.isEmpty() == true) {
            val historyTracks = interactor.getHistory()
            if (historyTracks.isNotEmpty()) {
                view.showHistory(historyTracks)
            } else {
                view.showTracks(emptyList())
            }
        } else {
            onSearch.invoke()
        }
    }

    fun loadTracks(query: String) {
        if (query.isEmpty()) {
            return
        }
        view.showLoading()
        interactor.loadTracks(
            query = query,
            onSuccess = {
                if (it.isEmpty()) {
                    view.showEmptyResult()
                } else {
                    view.showTracks(it)
                }
            },
            onError = {
                view.showSearchError()
            }
        )
    }

    fun searchTextClearClicked() {
        view.clearSearchText()
        view.hideKeyboard()
        view.showTracks(emptyList())
    }

    fun backButtonClicked() {
        router.goBack()
    }

    fun onHistoryTrackClicked(track: Track) {
        interactor.addTrack(track)
        router.openTrack(track)
    }
}