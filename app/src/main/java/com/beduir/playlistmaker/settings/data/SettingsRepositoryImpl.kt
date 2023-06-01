package com.beduir.playlistmaker.settings.data

import android.content.SharedPreferences
import android.content.res.Configuration
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.settings.domain.SettingsRepository
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {
    companion object {
        const val DARK_THEME_ENABLED = "dark_theme_enabled"
    }

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefs.getBoolean(DARK_THEME_ENABLED, getDarkThemeState()))
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