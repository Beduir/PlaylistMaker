package com.beduir.playlistmaker.search.presentation

import com.beduir.playlistmaker.search.domain.models.Track

sealed class SearchState {
    object NetworkIssue : SearchState()
    object NothingWasFound : SearchState()
    class History(val tracks: List<Track>) : SearchState()
    class Tracks(val tracks: List<Track>) : SearchState()
    object Loading : SearchState()
}