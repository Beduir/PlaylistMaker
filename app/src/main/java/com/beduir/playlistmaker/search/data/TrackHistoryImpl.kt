package com.beduir.playlistmaker.search.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.beduir.playlistmaker.search.domain.TrackHistory
import com.beduir.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val SEARCH_HISTORY = "search_history"
private const val HISTORY_SIZE = 10

class TrackHistoryImpl(
    context: Context,
    private val gson: Gson
) : TrackHistory {
    companion object {
        const val SEARCH_PREFERENCES = "search"
    }

    private var sharedPrefs: SharedPreferences = context.getSharedPreferences(
        SEARCH_PREFERENCES,
        Application.MODE_PRIVATE
    )

    override fun addTrack(track: Track) {
        val listString = getListString()
        if (listString == null) {
            saveList(arrayListOf(track))
            return
        }
        val trackList = listString.toTrackList()
        if (trackList.contains(track)) {
            trackList.remove(track)
        }
        if (trackList.size >= HISTORY_SIZE) {
            trackList.removeAt(trackList.size - 1)
        }
        trackList.add(0, track)
        saveList(trackList)
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY).apply()
    }

    override fun getHistory(): ArrayList<Track> =
        getListString()?.toTrackList() ?: ArrayList<Track>()

    private fun getListString(): String? = sharedPrefs.getString(SEARCH_HISTORY, null)

    private fun saveList(list: ArrayList<Track>) = sharedPrefs.edit().putString(
        SEARCH_HISTORY,
        gson.toJson(list)
    ).apply()

    private fun String.toTrackList() = gson.fromJson<ArrayList<Track>>(
        this, object : TypeToken<ArrayList<Track>>() {}.type
    )
}