package com.beduir.playlistmaker.settings.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.creator.Creator
import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings
import com.beduir.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val application: App,
) : AndroidViewModel(application) {

    private val darkThemeLiveData = MutableLiveData<Boolean>()

    init {
        darkThemeLiveData.value = settingsInteractor.getThemeSettings().darkTheme
    }

    fun observeDarkTheme(): LiveData<Boolean> = darkThemeLiveData

    fun onDarkThemeSwitchClicked(checked: Boolean) {
        darkThemeLiveData.value = checked
        settingsInteractor.setThemeSetting(ThemeSettings(checked))
        application.switchTheme(checked)
    }

    fun onShareButtonClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportButtonClicked() {
        sharingInteractor.openSupport()
    }

    fun onUserAgreementButtonClicked() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as App
                SettingsViewModel(
                    settingsInteractor = Creator.provideSettingsInteractor(application),
                    sharingInteractor = Creator.provideSharingInteractor(application),
                    application = application,
                )
            }
        }
    }
}