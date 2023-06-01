package com.beduir.playlistmaker.search.domain

import com.beduir.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
    fun addTrack(track: Track)
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}