package com.beduir.playlistmaker.search.domain.models

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return try {
            val date = formatter.parse(releaseDate)
            date?.let { getYearFromDate(it) } ?: ""
        } catch (e: ParseException) {
            ""
        }
    }

    private fun getYearFromDate(date: Date): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = date
        return calendar.get(Calendar.YEAR).toString()
    }
}