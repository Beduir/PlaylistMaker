package com.beduir.playlistmaker.settings.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.settings.domain.SettingsStorage
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

class SettingsStorageImpl(context: Context) : SettingsStorage {
    companion object {
        const val SETTINGS_PREFERENCES = "settings"
        const val DARK_THEME_ENABLED = "dark_theme_enabled"
    }

    private var sharedPrefs: SharedPreferences = context.getSharedPreferences(
        SETTINGS_PREFERENCES,
        Application.MODE_PRIVATE
    )

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            sharedPrefs.getBoolean(
                DARK_THEME_ENABLED,
                getDarkThemeState()
            )
        )
    }

    override fun setThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_ENABLED, settings.darkTheme)
            .apply()
    }

    private fun getDarkThemeState(): Boolean {
        return App.getInstance().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_YES == Configuration.UI_MODE_NIGHT_YES
    }
}