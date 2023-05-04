package com.beduir.playlistmaker.player.data

import android.media.MediaPlayer
import com.beduir.playlistmaker.player.domain.IPlayer

class Player: IPlayer {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private val player = MediaPlayer()

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        if (url.isEmpty()) return
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPrepared.invoke()
        }
        player.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion.invoke()
        }
    }

    override fun isPlayerPlaying(): Boolean {
        return playerState == STATE_PLAYING
    }

    override fun currentPosition(): Int {
        return player.currentPosition
    }

    @Suppress("UNUSED_EXPRESSION")
    override fun release() {
        player.setOnPreparedListener { null }
        player.setOnCompletionListener { null }
        player.release()
    }

    override fun pause() {
        if (isPlayerPlaying()) {
            playerState = STATE_PAUSED
            player.pause()
        }
    }

    override fun start() {
        if ((playerState == STATE_PREPARED) or (playerState == STATE_PAUSED)) {
            playerState = STATE_PLAYING
            player.start()
        }
    }
}