package com.beduir.playlistmaker.search.domain

import java.util.ArrayList

interface ITrackHistory {
    fun addTrack(track: Track)
    fun clearHistory()
    fun getHistory(): ArrayList<Track>
}