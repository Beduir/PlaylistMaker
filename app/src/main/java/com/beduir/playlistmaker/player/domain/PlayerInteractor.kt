package com.beduir.playlistmaker.player.domain

interface PlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun isPlayerPlaying(): Boolean
    fun currentPosition(): Int
    fun releasePlayer()
    fun playerPause()
    fun playerStart()
}