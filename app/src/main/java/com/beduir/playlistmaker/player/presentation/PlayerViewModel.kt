package com.beduir.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beduir.playlistmaker.player.domain.PlayerInteractor
import com.beduir.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    track: Track,
    private val interactor: PlayerInteractor
) : ViewModel() {

    companion object {
        private const val UPDATE_PLAYBACK_PROGRESS_DELAY = 300L
    }

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val playbackProgressLiveData = MutableLiveData<String>()
    fun observePlaybackProgress(): LiveData<String> = playbackProgressLiveData

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrack(): LiveData<Track> = trackLiveData

    private val handler = Handler(Looper.getMainLooper())

    init {
        renderState(PlayerState.Initializting)
        interactor.preparePlayer(
            url = track.previewUrl,
            onCompletion = {
                renderState(PlayerState.Paused)
                playbackProgressLiveData.postValue("00:00")
                handler.removeCallbacks(::updatePlaybackProgress)
            },
            onPrepared = {
                renderState(PlayerState.Paused)
            }
        )
        trackLiveData.postValue(track)
        playbackProgressLiveData.postValue("00:00")
    }

    override fun onCleared() {
        handler.removeCallbacks(::updatePlaybackProgress)
        interactor.releasePlayer()
    }

    fun playbackButton() {
        if (interactor.isPlayerPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun pause() {
        pausePlayer()
    }

    private fun startPlayer() {
        interactor.playerStart()
        renderState(PlayerState.Playing)
        handler.post(updatePlaybackProgress())
    }

    private fun pausePlayer() {
        interactor.playerPause()
        renderState(PlayerState.Paused)
        handler.removeCallbacks(::updatePlaybackProgress)
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    private fun updatePlaybackProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                if (interactor.isPlayerPlaying()) {
                    playbackProgressLiveData.postValue(
                        SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(interactor.currentPosition())
                    )
                    handler.postDelayed(this, UPDATE_PLAYBACK_PROGRESS_DELAY)
                }
            }
        }
    }
}