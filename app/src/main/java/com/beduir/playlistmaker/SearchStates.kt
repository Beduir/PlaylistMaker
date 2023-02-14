package com.beduir.playlistmaker

enum class SearchStates(val value: Int) {
    SEARCH_RESULT(1),
    NETWORK_ISSUES(2),
    NOTHING_WAS_FOUND(3);

    companion object {
        fun getByValue(value: Int) = SearchStates.values().firstOrNull { it.value == value }
    }
}