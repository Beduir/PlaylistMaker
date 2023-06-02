package com.beduir.playlistmaker.search.data.dto

data class TrackDto(
    val trackId: Int = 0,
    val trackName: String = "",
    val artistName: String = "",
    val trackTimeMillis: Int = 0,
    val artworkUrl100: String = "",
    val collectionName: String = "",
    val releaseDate: String = "",
    val primaryGenreName: String = "",
    val country: String = "",
    val previewUrl: String = ""
)