package com.beduir.playlistmaker.player.presentation

import com.beduir.playlistmaker.search.domain.Track

interface PlayerView {
    fun initPlayer()
    fun showTrackInfo(track: Track)
    fun playerPrepared()
    fun playbackCompleted()
    fun setPlaybackProgress(progress: String)
    fun playerPlaying()
    fun playerPaused()
}