package com.beduir.playlistmaker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.beduir.playlistmaker.creator.Creator

class App : Application() {
    companion object {
        private lateinit var instance: App
        const val SETTINGS_PREFERENCES = "settings"

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        switchTheme(Creator.provideSettingsInteractor(this).getThemeSettings().darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}