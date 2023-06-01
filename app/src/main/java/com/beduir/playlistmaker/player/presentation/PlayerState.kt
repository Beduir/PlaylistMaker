package com.beduir.playlistmaker.player.presentation

sealed class PlayerState {
    object Initializting : PlayerState()
    object Paused : PlayerState()
    object Playing : PlayerState()
}