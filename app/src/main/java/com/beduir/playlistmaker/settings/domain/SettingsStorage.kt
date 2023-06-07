package com.beduir.playlistmaker.settings.domain

import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsStorage {
    fun getThemeSettings(): ThemeSettings
    fun setThemeSetting(settings: ThemeSettings)
}