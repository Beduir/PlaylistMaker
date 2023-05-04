package com.beduir.playlistmaker.player.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.player.data.Player
import com.beduir.playlistmaker.player.domain.PlayerInteractor
import com.beduir.playlistmaker.search.domain.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity(), PlayerView {
    companion object {
        const val TRACK_VALUE = "track"
    }

    private lateinit var track: Track

    private var cornerRadius: Int = 0

    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var album: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var time: TextView
    private lateinit var play: ImageButton
    private lateinit var backButton: ImageView

    private lateinit var presenter: PlayerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        track = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(TRACK_VALUE) as? Track ?: Track()
        } else {
            intent.getSerializableExtra(TRACK_VALUE) as? Track ?: Track()
        }

        cornerRadius = this.resources.getDimensionPixelSize(
            R.dimen.player_cover_conver_radius
        )

        presenter = PlayerPresenter(
            view = this,
            track = track,
            interactor = PlayerInteractor(Player()),
            router = PlayerRouter(this),
            handler = Handler(Looper.getMainLooper())
        )

        backButton.setOnClickListener {
            presenter.backButtonClicked()
        }
        play.setOnClickListener {
            presenter.playButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(TRACK_VALUE, track)
    }

    override fun initPlayer() {
        play.isEnabled = false
    }

    override fun showTrackInfo(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.cover)
            .fitCenter()
            .transform(RoundedCorners(cornerRadius))
            .into(cover)
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        if (track.collectionName.isEmpty()) {
            trackAlbum.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            trackAlbum.text = track.collectionName
        }
        trackYear.text = track.getYear()
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
    }

    override fun playerPrepared() {
        play.isEnabled = true
    }

    override fun playbackCompleted() {
        play.setImageResource(R.drawable.play_button)
    }

    override fun setPlaybackProgress(progress: String) {
        time.text = progress
    }

    override fun playerPaused() {
        play.setImageResource(R.drawable.play_button)
    }

    override fun playerPlaying() {
        play.setImageResource(R.drawable.pause_button)
    }

    private fun initViews() {
        cover = findViewById(R.id.cover)
        trackName = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        trackDuration = findViewById(R.id.duration_value)
        album = findViewById(R.id.album)
        trackAlbum = findViewById(R.id.album_value)
        trackYear = findViewById(R.id.year_value)
        trackGenre = findViewById(R.id.genre_value)
        trackCountry = findViewById(R.id.country_value)
        time = findViewById(R.id.time)
        play = findViewById(R.id.play)
        backButton = findViewById(R.id.back_button)
    }
}