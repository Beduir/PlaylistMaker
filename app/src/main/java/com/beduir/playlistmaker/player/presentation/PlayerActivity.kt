package com.beduir.playlistmaker.player.presentation

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beduir.playlistmaker.R
import com.beduir.playlistmaker.creator.Creator
import com.beduir.playlistmaker.search.domain.models.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_VALUE = "track"
    }

    private lateinit var viewModel: PlayerViewModel

    private lateinit var router: PlayerRouter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        cornerRadius = this.resources.getDimensionPixelSize(
            R.dimen.player_cover_conver_radius
        )

        router = PlayerRouter(this)

        viewModel = ViewModelProvider(
            this, PlayerViewModelFactory(
                intent.getSerializableExtra(TRACK_VALUE) as? Track ?: Track(),
                Creator.providePlayerInteractor()
            )
        )[PlayerViewModel::class.java]

        backButton.setOnClickListener {
            router.goBack()
        }
        play.setOnClickListener {
            viewModel.playbackButton()
        }

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observePlaybackProgress().observe(this) {
            time.text = it
        }
        viewModel.observeTrack().observe(this) {
            showTrackInfo(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
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

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Initializting -> play.isEnabled = false
            is PlayerState.Paused -> {
                play.isEnabled = true
                play.setImageResource(R.drawable.play_button)
            }

            is PlayerState.Playing -> {
                play.isEnabled = true
                play.setImageResource(R.drawable.pause_button)
            }
        }
    }

    private fun showTrackInfo(track: Track) {
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
}