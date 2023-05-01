package com.beduir.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_VALUE = "track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_PLAYBACK_PROGRESS_DELAY = 300L
    }

    private var playerState = STATE_DEFAULT

    private var track: Track? = null

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

    private var mediaPlayer = MediaPlayer()

    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backButton = findViewById<ImageView>(R.id.back_button)

        initViews()
        play.isEnabled = false

        if (savedInstanceState != null) {
            track = savedInstanceState.getSerializable(TRACK_VALUE) as? Track
        } else {
            track = intent.getSerializableExtra(TRACK_VALUE) as? Track
        }

        cornerRadius = this.resources.getDimensionPixelSize(
            R.dimen.player_cover_conver_radius
        )

        backButton.setOnClickListener {
            finish()
        }

        preparePlayer()

        play.setOnClickListener {
            playbackControl()
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        displayTrackInfo(track!!)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(::updatePlaybackProgress)
        mediaPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(TRACK_VALUE, track)
    }

    fun getCoverArtwork(artworkUrl100: String) =
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    private fun initViews() {
        cover = findViewById<ImageView>(R.id.cover)
        trackName = findViewById<TextView>(R.id.track_name)
        trackArtist = findViewById<TextView>(R.id.track_artist)
        trackDuration = findViewById<TextView>(R.id.duration_value)
        album = findViewById<TextView>(R.id.album)
        trackAlbum = findViewById<TextView>(R.id.album_value)
        trackYear = findViewById<TextView>(R.id.year_value)
        trackGenre = findViewById<TextView>(R.id.genre_value)
        trackCountry = findViewById<TextView>(R.id.country_value)
        time = findViewById<TextView>(R.id.time)
        play = findViewById<ImageButton>(R.id.play)
    }

    private fun displayTrackInfo(track: Track) {
        Glide.with(this)
            .load(getCoverArtwork(track.artworkUrl100))
            .placeholder(R.drawable.cover)
            .fitCenter()
            .transform(RoundedCorners(cornerRadius))
            .into(cover)
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)
        if (track.collectionName.isNullOrEmpty()) {
            trackAlbum.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            trackAlbum.text = track.collectionName
        }
        trackYear.text = getYearFromDate(track.releaseDate!!)
        trackGenre.text = track.primaryGenreName!!
        trackCountry.text = track.country!!
        time.text = "00:00"
    }

    private fun getYearFromDate(date_string: String): String {
        var year = ""
        try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .parse(date_string)
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.time = date
            year = calendar.get(Calendar.YEAR).toString()
        } catch (e: ParseException) {
        } catch (e: IllegalArgumentException) {
        }
        return year
    }

    private fun preparePlayer() {
        if (track!!.previewUrl.isNullOrEmpty()) {
            return
        }
        mediaPlayer.setDataSource(track!!.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            time.text = "00:00"
            mainThreadHandler?.removeCallbacks(::updatePlaybackProgress)
        }
    }

    private fun updatePlaybackProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    time.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(this, UPDATE_PLAYBACK_PROGRESS_DELAY)
                }
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(
            updatePlaybackProgress()
        )
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(::updatePlaybackProgress)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
}