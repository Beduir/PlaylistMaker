package com.beduir.playlistmaker.search.domain

import com.beduir.playlistmaker.search.domain.models.Track

interface TrackHistory {
    fun addTrack(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}