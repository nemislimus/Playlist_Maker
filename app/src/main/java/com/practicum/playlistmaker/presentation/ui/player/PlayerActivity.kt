package com.practicum.playlistmaker.presentation.ui.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.presentation.ui.PlaylistApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.search.TrackViewHolder
import com.practicum.playlistmaker.presentation.ui.createTrackFromJson
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.entities.PlayerState
import com.practicum.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var backToSearchButton: Toolbar
    private lateinit var albumCover: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistTitle: TextView

    private lateinit var playButton: ImageView
    private lateinit var addToPlaylist: ImageView
    private lateinit var addToFavorite: ImageView
    private lateinit var trackTimer: TextView

    private lateinit var trackDuration: TextView
    private lateinit var trackDurationValue: TextView
    private lateinit var albumLine: Group
    private lateinit var albumValue: TextView
    private lateinit var releaseDate: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var genre: TextView
    private lateinit var genreValue: TextView
    private lateinit var country: TextView
    private lateinit var countryValue: TextView

    private var hasCollection: Boolean = true
    private val playerActivityHandler = Handler(Looper.getMainLooper())

    private lateinit var currentTrack: Track
    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false

    private val playerInteractor = Creator.provideMediaPlayaerInteractor()
    private var timerRunnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentTrack =
            intent.getStringExtra(PlaylistApp.TRACK_KEY)?.let { createTrackFromJson(it) }!!

        backToSearchButton = findViewById(R.id.tbBackFromPlayerButton)
        albumCover = findViewById(R.id.ivAlbumCover)
        trackTitle = findViewById(R.id.tvTrackNamePlayer)
        artistTitle = findViewById(R.id.tvArtistNamePlayer)

        playButton = findViewById<ImageView?>(R.id.ivPlayButton).apply { isEnabled = false }
        addToPlaylist = findViewById(R.id.ivAddToPlaylist)
        addToFavorite = findViewById(R.id.ivAddToFavorites)
        trackTimer = findViewById(R.id.tvTrackTimer)

        trackDuration = findViewById(R.id.tvTrackDuration)
        trackDurationValue = findViewById(R.id.tvTrackDurationValue)
        albumLine = findViewById(R.id.albumGroup)
        albumValue = findViewById(R.id.tvTrackAlbumValue)
        releaseDate = findViewById(R.id.tvTrackReleaseDate)
        releaseDateValue = findViewById(R.id.tvTrackReleaseDateValue)
        genre = findViewById(R.id.tvTrackGenre)
        genreValue = findViewById(R.id.tvTrackGenreValue)
        country = findViewById(R.id.tvTrackCountry)
        countryValue = findViewById(R.id.tvTrackCountryValue)

        setUiValues(currentTrack)

        preparePlayer()

        playButton.setOnClickListener {
            when(playerInteractor.getPlayerState()) {
                PlayerState.PREPARED, PlayerState.PAUSED -> playerInteractor.start()
                PlayerState.PLAYING -> playerInteractor.pause()
                PlayerState.DEFAULT -> {}
            }
        }

        addToPlaylist.setOnClickListener {
            trackInPlaylist = !trackInPlaylist
            setButtonState(it, trackInPlaylist)
        }

        addToFavorite.setOnClickListener {
            trackInFavorites = !trackInFavorites
            setButtonState(it, trackInFavorites)
        }

        backToSearchButton.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
        manageTimer() // clean by playerActivityHandler.removeCallbacksAndMessages(null) inside
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IN_PLAYLIST_VALUE, trackInPlaylist)
        outState.putBoolean(IN_FAVORITE_VALUE, trackInFavorites)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackInPlaylist = savedInstanceState.getBoolean(IN_PLAYLIST_VALUE)
        trackInFavorites = savedInstanceState.getBoolean(IN_FAVORITE_VALUE)

        setButtonState(addToPlaylist, trackInPlaylist)
        setButtonState(addToFavorite, trackInFavorites)
    }

    private fun setUiValues(track: Track?) {
        if (track?.collectionName.isNullOrEmpty()) {
            hasCollection = false
            albumLine.isVisible = false
        }

        track?.let {
            Glide.with(this)
                .load(track.artworkUrl512)
                .placeholder(R.drawable.ic_placeholder_track_image)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.dpToPx(8f, this)))
                .into(albumCover)

            if (hasCollection) albumValue.text = track.collectionName
            trackTitle.text = track.trackName
            artistTitle.text = track.artistName
            trackDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            trackTimer.text = ZERO_TIMER
            releaseDateValue.text = track.releaseDate.substring(0, 4)
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country
        }
    }

    private fun preparePlayer() {
        playerInteractor.setPlayerDataSource(currentTrack)
        playerInteractor.prepare(
            listener = object: PlayerInteractor.OnStateChangeListener {
                override fun onChange(state: PlayerState) {
                    when(state) {
                        PlayerState.PREPARED -> {
                            playButton.isEnabled = true
                            playButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }

                        PlayerState.PLAYING -> {
                            playButton.setImageResource(R.drawable.pause_button)
                            manageTimer()
                        }

                        PlayerState.PAUSED -> {
                            playButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }

                        PlayerState.DEFAULT -> {
                            playButton.isEnabled = false
                            playButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }
                    }
                }
            }
        )
    }

    private fun setButtonState(actionView: View, state: Boolean) {
        when {
            (actionView == addToPlaylist) -> (actionView as ImageView)
                .setImageResource(if (state) R.drawable.added_in_playlist else R.drawable.add_to_playlist)

            (actionView == addToFavorite) -> (actionView as ImageView)
                .setImageResource(if (state) R.drawable.added_in_favorites else R.drawable.add_to_favorite)
        }
    }

    private fun manageTimer() {
        manageTimerByPlayerState(playerInteractor.getPlayerState())
    }

    private fun manageTimerByPlayerState(state: PlayerState) {
        when (state) {
            PlayerState.DEFAULT, PlayerState.PREPARED -> {
                playerActivityHandler.removeCallbacksAndMessages(null)
                trackTimer.text = ZERO_TIMER
            }

            PlayerState.PLAYING -> {
                val runnable = object : Runnable {
                    override fun run() {
                        trackTimer.text = playerInteractor.getPlayerCurrentTimerPosition()
                        playerActivityHandler.postDelayed(this, TIMER_DELAY)
                    }
                }

                playerActivityHandler.postDelayed(runnable, TIMER_DELAY)
                timerRunnable = runnable
            }

            PlayerState.PAUSED -> timerRunnable?.let { playerActivityHandler.removeCallbacksAndMessages(timerRunnable) }
        }
    }

    companion object {
        private const val ZERO_TIMER = "00:00"
        private const val TIMER_DELAY = 400L

        private const val IN_PLAYLIST_VALUE = "IN_PLAYLIST_VALUE"
        private const val IN_FAVORITE_VALUE = "IN_FAVORITE_VALUE"
    }
}

