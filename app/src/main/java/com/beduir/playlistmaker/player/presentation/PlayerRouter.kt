package com.beduir.playlistmaker.player.presentation

import androidx.appcompat.app.AppCompatActivity

class PlayerRouter(
    private val activity: AppCompatActivity
) {
    fun goBack() {
        activity.finish()
    }
}