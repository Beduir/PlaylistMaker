package com.beduir.playlistmaker.settings.domain

import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun setThemeSetting(settings: ThemeSettings)
}