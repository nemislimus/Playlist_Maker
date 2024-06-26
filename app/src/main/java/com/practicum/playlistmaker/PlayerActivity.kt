package com.practicum.playlistmaker

import android.media.MediaPlayer
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
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var trackOnAir: Boolean? = false
    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentTrack =
            intent.getStringExtra(PlaylistApp.TRACK_KEY)?.let { createTrackFromJson(it) }!!

        backToSearchButton = findViewById(R.id.tbBackFromPlayerButton)
        albumCover = findViewById(R.id.ivAlbumCover)
        trackTitle = findViewById(R.id.tvTrackNamePlayer)
        artistTitle = findViewById(R.id.tvArtistNamePlayer)

        playButton = findViewById(R.id.ivPlayButton)
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

        playButton.isEnabled = false
        prepareMediaPlayer()


        // Кнопка "назад к поиску"
        backToSearchButton.setOnClickListener {
            finish()
        }

        // Проверяем наличие названия альбома
        if (currentTrack?.collectionName.isNullOrEmpty()) {
            hasCollection = false
            albumLine.isVisible = false
        }

        //Наполняем UI данными из currentTrack
        currentTrack?.let {
            Glide.with(this)
                .load(currentTrack.artworkUrl512)
                .placeholder(R.drawable.ic_placeholder_track_image)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.dpToPx(8f, this)))
                .into(albumCover)

            if (hasCollection) albumValue.text = currentTrack.collectionName

            trackTitle.text = currentTrack.trackName
            artistTitle.text = currentTrack.artistName
            trackDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis)
            trackTimer.text = ZERO_TIMER
            releaseDateValue.text = currentTrack.releaseDate.substring(0, 4)
            genreValue.text = currentTrack.primaryGenreName
            countryValue.text = currentTrack.country
        }

        // Нажатие на Play
        playButton.setOnClickListener {
            playbackControl()
            manageTimer(trackOnAir)
        }

        // Нажатие на addToPlaylist
        addToPlaylist.setOnClickListener {
            trackInPlaylist = !trackInPlaylist
            setButtonState(it, trackInPlaylist)
        }

        // Нажатие на addToFavorites
        addToFavorite.setOnClickListener {
            trackInFavorites = !trackInFavorites
            setButtonState(it, trackInFavorites)
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) trackOnAir = !trackOnAir!!
        pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        manageTimer(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ON_AIR_VALUE, trackOnAir!!)
        outState.putBoolean(IN_PLAYLIST_VALUE, trackInPlaylist)
        outState.putBoolean(IN_FAVORITE_VALUE, trackInFavorites)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackOnAir = savedInstanceState.getBoolean(ON_AIR_VALUE)
        trackInPlaylist = savedInstanceState.getBoolean(IN_PLAYLIST_VALUE)
        trackInFavorites = savedInstanceState.getBoolean(IN_FAVORITE_VALUE)

        // Устанавливаем отображение кнопочек
        setButtonState(addToPlaylist, trackInPlaylist)
        setButtonState(addToFavorite, trackInFavorites)
    }

    private fun setButtonState(actionView: View, state: Boolean) {
        when {
            (actionView == addToPlaylist) -> (actionView as ImageView)
                .setImageResource(if (state) R.drawable.added_in_playlist else R.drawable.add_to_playlist)

            (actionView == addToFavorite) -> (actionView as ImageView)
                .setImageResource(if (state) R.drawable.added_in_favorites else R.drawable.add_to_favorite)
        }
    }

    private fun prepareMediaPlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play_button)
            manageTimer(null)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                trackOnAir = !trackOnAir!!
                pauseMediaPlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startMediaPlayer()
            }
        }
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        trackOnAir = !trackOnAir!!
        playButton.setImageResource(R.drawable.pause_button)
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_button)
    }

    private fun manageTimer(onAir: Boolean?) {
        when (onAir) {
            true -> {
                playerActivityHandler.postDelayed(
                    object : Runnable {
                        override fun run() {
                            trackTimer.text =
                                SimpleDateFormat("mm:ss", Locale.getDefault())
                                    .format(mediaPlayer.currentPosition.toLong())

                            playerActivityHandler.postDelayed(this, TIMER_DELAY)
                        }
                    },
                    TIMER_DELAY
                )
            }

            false -> playerActivityHandler.removeCallbacksAndMessages(null)
            null -> {
                playerActivityHandler.removeCallbacksAndMessages(null)
                trackOnAir = false
                trackTimer.text = ZERO_TIMER
            }
        }
    }

    companion object {
        private const val ZERO_TIMER = "00:00"
        private const val TIMER_DELAY = 400L

        private const val ON_AIR_VALUE = "ON_AIR_VALUE"
        private const val IN_PLAYLIST_VALUE = "IN_PLAYLIST_VALUE"
        private const val IN_FAVORITE_VALUE = "IN_FAVORITE_VALUE"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}

