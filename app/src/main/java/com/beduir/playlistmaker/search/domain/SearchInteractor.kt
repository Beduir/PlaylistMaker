package com.beduir.playlistmaker.search.domain

import java.util.ArrayList

class SearchInteractor(
    private val trackHistory: ITrackHistory,
    private val repository: ISearchRepository
): ISearchInteractor {
    override fun clearHistory() {
        trackHistory.clearHistory()
    }

    override fun getHistory(): ArrayList<Track> {
        return trackHistory.getHistory()
    }

    override fun addTrack(track: Track) {
        trackHistory.addTrack(track)
    }

    override fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit) {
        repository.loadTracks(query, onSuccess, onError)
    }
}

interface ISearchInteractor {
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
    fun addTrack(track: Track)
    fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit)
}