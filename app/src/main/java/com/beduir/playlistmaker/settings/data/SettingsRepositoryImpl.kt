package com.beduir.playlistmaker.settings.data

import com.beduir.playlistmaker.settings.domain.SettingsRepository
import com.beduir.playlistmaker.settings.domain.SettingsStorage
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val settingsStorage: SettingsStorage
) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return settingsStorage.getThemeSettings()
    }

    override fun setThemeSetting(settings: ThemeSettings) {
        settingsStorage.setThemeSetting(settings)
    }
}