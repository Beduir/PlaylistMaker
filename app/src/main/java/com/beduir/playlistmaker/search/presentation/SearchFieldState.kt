package com.beduir.playlistmaker.search.presentation

sealed class SearchFieldState {
    class SearchTextEmpty(val needClear: Boolean) : SearchFieldState()
    object SearchTextEntered : SearchFieldState()
}