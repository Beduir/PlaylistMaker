package com.beduir.playlistmaker.player.domain

interface IPlayer {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun isPlayerPlaying(): Boolean
    fun currentPosition(): Int
    fun release()
    fun pause()
    fun start()
}