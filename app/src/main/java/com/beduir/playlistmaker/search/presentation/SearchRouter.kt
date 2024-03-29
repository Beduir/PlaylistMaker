package com.beduir.playlistmaker.search.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beduir.playlistmaker.player.presentation.PlayerActivity
import com.beduir.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SearchRouter(
    private val activity: AppCompatActivity
) {
    fun openTrack(track: Track) {
        val playerIntent = Intent(activity, PlayerActivity::class.java)
        playerIntent.putExtra(PlayerActivity.TRACK_VALUE, Gson().toJson(track))
        activity.startActivity(playerIntent)
    }

    fun goBack() {
        activity.finish()
    }
}