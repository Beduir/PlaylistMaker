package com.beduir.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beduir.playlistmaker.application.App
import com.beduir.playlistmaker.settings.domain.SettingsInteractor
import com.beduir.playlistmaker.settings.domain.model.ThemeSettings
import com.beduir.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val darkThemeLiveData = MutableLiveData<Boolean>()

    init {
        darkThemeLiveData.value = settingsInteractor.getThemeSettings().darkTheme
    }

    fun observeDarkTheme(): LiveData<Boolean> = darkThemeLiveData

    fun onDarkThemeSwitchClicked(checked: Boolean) {
        darkThemeLiveData.value = checked
        settingsInteractor.setThemeSetting(ThemeSettings(checked))
        App.getInstance().switchTheme(checked)
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

//    companion object {
//        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = this[APPLICATION_KEY] as App
//                SettingsViewModel(
//                    settingsInteractor = Creator.provideSettingsInteractor(application),
//                    sharingInteractor = Creator.provideSharingInteractor(application),
//                )
//            }
//        }
//    }
}