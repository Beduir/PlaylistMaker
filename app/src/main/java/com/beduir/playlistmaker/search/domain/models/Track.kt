package com.beduir.playlistmaker.search.domain.models

import com.beduir.playlistmaker.util.DateTimeUtil
import java.io.Serializable

private const val TRACK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

data class Track(
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
) : Serializable {
    fun getCoverArtwork(): String =
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getYear(): String {
        return DateTimeUtil.getYear(TRACK_DATE_FORMAT, releaseDate)
    }
}