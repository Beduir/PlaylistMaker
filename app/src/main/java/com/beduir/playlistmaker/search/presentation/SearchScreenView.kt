package com.beduir.playlistmaker.search.presentation

import com.beduir.playlistmaker.search.domain.Track

interface SearchScreenView {
    fun showHistory(tracks: ArrayList<Track>)
    fun showEmptyResult()
    fun showTracks(tracks: List<Track>)
    fun showSearchError()
    fun hideKeyboard()
    fun clearSearchText()
    fun showLoading()
}