package com.beduir.playlistmaker.player.presentation

import android.os.Handler
import com.beduir.playlistmaker.player.domain.IPlayerInteractor
import com.beduir.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerPresenter(
    private val view: PlayerView,
    track: Track,
    private val interactor: IPlayerInteractor,
    private val router: PlayerRouter,
    private val handler: Handler
) {
    companion object {
        private const val UPDATE_PLAYBACK_PROGRESS_DELAY = 300L
    }

    init {
        view.initPlayer()
        interactor.preparePlayer(
            url = track.previewUrl,
            onCompletion = {
                view.playbackCompleted()
                view.setPlaybackProgress("00:00")
                handler.removeCallbacks(::updatePlaybackProgress)
            },
            onPrepared = {
                view.playerPrepared()
            }
        )
        view.showTrackInfo(track)
        view.setPlaybackProgress("00:00")
    }

    fun backButtonClicked() {
        router.goBack()
    }

    fun onViewDestroyed() {
        handler.removeCallbacks(::updatePlaybackProgress)
        interactor.releasePlayer()
    }

    fun playButtonClicked() {
        if (interactor.isPlayerPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun onViewPaused() {
        pausePlayer()
    }

    private fun startPlayer() {
        interactor.playerStart()
        view.playerPlaying()
        handler.post(updatePlaybackProgress())
    }

    private fun pausePlayer() {
        interactor.playerPause()
        view.playerPaused()
        handler.removeCallbacks(::updatePlaybackProgress)
    }

    private fun updatePlaybackProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                if (interactor.isPlayerPlaying()) {
                    view.setPlaybackProgress(SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(interactor.currentPosition()))
                    handler.postDelayed(this, UPDATE_PLAYBACK_PROGRESS_DELAY)
                }
            }
        }
    }
}