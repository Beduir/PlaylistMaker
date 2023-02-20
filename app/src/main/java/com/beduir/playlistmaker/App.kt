package com.beduir.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val SETTINGS_PREFERENCES = "settings"
const val DARK_THEME_ENABLED = "dark_theme_enabled"

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        val sharedPrefs = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
        super.onCreate()
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_ENABLED, getDarkThemeState())
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun getDarkThemeState(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_YES == Configuration.UI_MODE_NIGHT_YES
    }
}