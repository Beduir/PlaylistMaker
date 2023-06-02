package com.beduir.playlistmaker.search.domain

import com.beduir.playlistmaker.search.domain.models.Track
import com.beduir.playlistmaker.util.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}