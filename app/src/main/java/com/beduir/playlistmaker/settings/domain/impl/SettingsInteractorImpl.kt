package com.beduir.playlistmaker.settings.domain.impl

import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import com.beduir.playlistmaker.settings.domain.SettingsRepository
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun setThemeSetting(settings: ThemeSettings) {
        repository.setThemeSetting(settings)
    }
}