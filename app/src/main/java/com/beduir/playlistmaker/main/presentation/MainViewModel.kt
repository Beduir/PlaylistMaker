package com.beduir.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainViewModel : ViewModel() {
    private val menuLiveData = SingleLiveEvent<MainMenuItems>()

    fun observeMenu(): LiveData<MainMenuItems> = menuLiveData

    fun onSearchButtonClicked() {
        menuLiveData.value = MainMenuItems.Search
    }

    fun onMediaButtonClicked() {
        menuLiveData.value = MainMenuItems.Media
    }

    fun onSettingsButtonClicked() {
        menuLiveData.value = MainMenuItems.Settings
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel()
            }
        }
    }
}