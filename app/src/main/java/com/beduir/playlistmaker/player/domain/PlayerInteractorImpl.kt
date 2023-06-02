package com.beduir.playlistmaker.player.domain

class PlayerInteractorImpl(
    private val player: Player
) : PlayerInteractor {
    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        player.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun isPlayerPlaying(): Boolean {
        return player.isPlayerPlaying()
    }

    override fun currentPosition(): Int {
        return player.currentPosition()
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun playerPause() {
        player.pause()
    }

    override fun playerStart() {
        player.start()
    }
}