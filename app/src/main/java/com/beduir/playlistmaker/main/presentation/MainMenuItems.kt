package com.beduir.playlistmaker.main.presentation

sealed interface MainMenuItems {
    object Search : MainMenuItems
    object Media : MainMenuItems
    object Settings : MainMenuItems
}