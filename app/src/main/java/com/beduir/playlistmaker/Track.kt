package com.beduir.playlistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
)

class TracksResponse(
    val results: List<Track>
)