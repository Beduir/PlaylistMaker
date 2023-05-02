package com.beduir.playlistmaker.search.domain

interface ISearchRepository {
    fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit)
}