package com.beduir.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beduir.playlistmaker.search.domain.SearchInteractor
import com.beduir.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val interactor: SearchInteractor,
    private val router: SearchRouter
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val SEARCH_REFRESH_DEBOUNCE_DELAY = 500L
        private val SEARCH_REQUEST_TOKEN = Any()
        private val SEARCH_REFRESH_REQUEST_TOKEN = Any()
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val stateSearchFieldLiveData = MutableLiveData<SearchFieldState>()
    fun observeStateSearchField(): LiveData<SearchFieldState> = stateSearchFieldLiveData

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    init {
        renderState(SearchState.Tracks(tracks = emptyList()))
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        handler.removeCallbacksAndMessages(SEARCH_REFRESH_REQUEST_TOKEN)
    }

    fun clearSearchText() {
        stateSearchFieldLiveData.postValue(SearchFieldState.SearchTextEmpty(true))
        renderState(SearchState.Tracks(tracks = emptyList()))
    }

    fun searchDebounce(changedText: String) {
        if (changedText.isEmpty()) {
            stateSearchFieldLiveData.postValue(SearchFieldState.SearchTextEmpty(false))
        } else {
            stateSearchFieldLiveData.postValue(SearchFieldState.SearchTextEntered)
        }
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun refreshSearch(newSearchText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REFRESH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(newSearchText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_REFRESH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REFRESH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchTextChangeFocus(hasFocus: Boolean, text: String) {
        val historyTracks = interactor.getHistory()
        if (hasFocus && text.isEmpty() && historyTracks.isNotEmpty()) {
            renderState(SearchState.History(tracks = historyTracks))
        }
    }

    fun clearHistory() {
        interactor.clearHistory()
        renderState(SearchState.Tracks(tracks = emptyList()))
    }

    fun openTrack(track: Track) {
        interactor.addTrack(track)
        router.openTrack(track)
    }

    fun goBack() {
        router.goBack()
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty()) return
        renderState(SearchState.Loading)
        interactor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = mutableListOf<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }

                when {
                    errorMessage != null -> renderState(SearchState.NetworkIssue)
                    tracks.isEmpty() -> renderState(SearchState.NothingWasFound)
                    else -> renderState(SearchState.Tracks(tracks))
                }
            }
        })
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}