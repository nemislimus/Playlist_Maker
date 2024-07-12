package com.practicum.playlistmaker.presentation.ui.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.presentation.ui.PlaylistApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.search.TrackViewHolder
import com.practicum.playlistmaker.presentation.ui.createTrackFromJson
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.entities.PlayerState
import com.practicum.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private var hasCollection: Boolean = true
    private val playerActivityHandler = Handler(Looper.getMainLooper())

    private lateinit var currentTrack: Track
    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false

    private val playerInteractor = Creator.provideMediaPlayaerInteractor()
    private var timerRunnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack =
            intent.getStringExtra(PlaylistApp.TRACK_KEY)?.let { createTrackFromJson(it) }!!

        binding.ivPlayButton.apply { isEnabled = false }

        setUiValues(currentTrack)

        preparePlayer()

        binding.ivPlayButton.setOnClickListener {
            when(playerInteractor.getPlayerState()) {
                PlayerState.PREPARED, PlayerState.PAUSED -> playerInteractor.start()
                PlayerState.PLAYING -> playerInteractor.pause()
                PlayerState.DEFAULT -> {}
            }
        }

        binding.ivAddToPlaylistButton.setOnClickListener {
            trackInPlaylist = !trackInPlaylist
            setButtonState(it, trackInPlaylist)
        }

        binding.ivAddToFavoritesButton.setOnClickListener {
            trackInFavorites = !trackInFavorites
            setButtonState(it, trackInFavorites)
        }

        binding.tbBackFromPlayerButton.setOnClickListener {
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

        setButtonState(binding.ivAddToPlaylistButton, trackInPlaylist)
        setButtonState(binding.ivAddToFavoritesButton, trackInFavorites)
    }

    private fun setUiValues(track: Track?) {
        if (track?.collectionName.isNullOrEmpty()) {
            hasCollection = false
            binding.albumInfoGroup.isVisible = false
        }

        track?.let {
            Glide.with(this)
                .load(track.artworkUrl512)
                .placeholder(R.drawable.ic_placeholder_track_image)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.dpToPx(8f, this)))
                .into(binding.ivAlbumCover)

            if (hasCollection) binding.tvTrackAlbumValue.text = track.collectionName
            binding.tvTrackNamePlayer.text = track.trackName
            binding.tvArtistNamePlayer.text = track.artistName
            binding.tvTrackDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            binding.tvTrackTimer.text = ZERO_TIMER
            binding.tvTrackReleaseDateValue.text = track.releaseDate.substring(0, 4)
            binding.tvTrackGenreValue.text = track.primaryGenreName
            binding.tvTrackCountryValue.text = track.country
        }
    }

    private fun preparePlayer() {
        playerInteractor.setPlayerDataSource(currentTrack)
        playerInteractor.prepare(
            listener = object: PlayerInteractor.OnStateChangeListener {
                override fun onChange(state: PlayerState) {
                    when(state) {
                        PlayerState.PREPARED -> {
                            binding.ivPlayButton.isEnabled = true
                            binding.ivPlayButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }

                        PlayerState.PLAYING -> {
                            binding.ivPlayButton.setImageResource(R.drawable.pause_button)
                            manageTimer()
                        }

                        PlayerState.PAUSED -> {
                            binding.ivPlayButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }

                        PlayerState.DEFAULT -> {
                            binding.ivPlayButton.isEnabled = false
                            binding.ivPlayButton.setImageResource(R.drawable.play_button)
                            manageTimer()
                        }
                    }
                }
            }
        )
    }

    private fun setButtonState(actionView: View, state: Boolean) {
        when {
            (actionView == binding.ivAddToPlaylistButton) -> (actionView as ImageView)
                .setImageResource(if (state) R.drawable.added_in_playlist else R.drawable.add_to_playlist)

            (actionView == binding.ivAddToFavoritesButton) -> (actionView as ImageView)
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
                binding.tvTrackTimer.text = ZERO_TIMER
            }

            PlayerState.PLAYING -> {
                val runnable = object : Runnable {
                    override fun run() {
                        binding.tvTrackTimer.text = playerInteractor.getPlayerCurrentTimerPosition()
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

