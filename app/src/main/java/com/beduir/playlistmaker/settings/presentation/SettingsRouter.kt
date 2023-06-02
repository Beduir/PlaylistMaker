package com.beduir.playlistmaker.settings.presentation

import androidx.activity.ComponentActivity

class SettingsRouter(
    private val activity: ComponentActivity
) {
    fun goBack() {
        activity.finish()
    }
}