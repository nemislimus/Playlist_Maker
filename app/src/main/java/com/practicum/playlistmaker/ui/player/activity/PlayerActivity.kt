package com.practicum.playlistmaker.ui.player.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.createTrackFromJson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.ui.player.model.PlayerUiState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false

    private var timerRunnable: Runnable? = null
    private val playerActivityHandler = Handler(Looper.getMainLooper())

    private val viewModel: PlayerViewModel by viewModel{
        parametersOf(intent.getStringExtra(PlaylistApp.TRACK_KEY)?.let { createTrackFromJson(it) }!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivPlayButton.setOnClickListener {
            when(viewModel.getPlayerState()) {
                PlayerState.PREPARED, PlayerState.PAUSED -> viewModel.playTrack()
                PlayerState.PLAYING -> viewModel.pauseTrack()
                PlayerState.DEFAULT -> Unit
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

        viewModel.apply {
            getPlayerUiStateLiveData().observe(this@PlayerActivity) { stateUi ->
                setUi(stateUi)
            }

            getPlayerStateLiveData().observe(this@PlayerActivity) { playerState ->
                managePlayer(playerState)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setPlayButtonAsPrepared { updatePlayButtonAlpha() }
        viewModel.preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerActivityHandler.removeCallbacksAndMessages(null)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IN_PLAYLIST_VALUE, trackInPlaylist)
        outState.putBoolean(IN_FAVORITE_VALUE, trackInFavorites)
        outState.putString(VALUE_TIMER, binding.tvTrackTimer.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackInPlaylist = savedInstanceState.getBoolean(IN_PLAYLIST_VALUE)
        trackInFavorites = savedInstanceState.getBoolean(IN_FAVORITE_VALUE)
        val timerValue = savedInstanceState.getString(VALUE_TIMER).toString()

        binding.tvTrackTimer.text = timerValue
        setButtonState(binding.ivAddToPlaylistButton, trackInPlaylist)
        setButtonState(binding.ivAddToFavoritesButton, trackInFavorites)
    }

    private fun setUi(stateUi: PlayerUiState) {

        Glide.with(this)
            .load(stateUi.coverLink)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .centerCrop()
            .transform(stateUi.coverCornerRadius)
            .into(binding.ivAlbumCover)

        binding.albumInfoGroup.isVisible = stateUi.hasCollection
        if (stateUi.hasCollection) binding.tvTrackAlbumValue.text = stateUi.collectionName
        binding.tvTrackNamePlayer.text = stateUi.trackName
        binding.tvArtistNamePlayer.text = stateUi.artistName
        binding.tvTrackDurationValue.text = stateUi.trackDuration
        binding.tvTrackTimer.text = ZERO_TIMER
        binding.tvTrackReleaseDateValue.text = stateUi.releaseDate
        binding.tvTrackGenreValue.text = stateUi.genreName
        binding.tvTrackCountryValue.text = stateUi.country
    }

    private fun updatePlayButtonAlpha() {
        binding.ivPlayButton.alpha = 1.0f
    }

    private fun managePlayer(playerState: PlayerState) {
        when(playerState) {
             PlayerState.PREPARED -> {
                binding.ivPlayButton.isEnabled = true
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                 manageTimerByPlayerState(playerState)
            }

            PlayerState.PLAYING -> {
                binding.ivPlayButton.setImageResource(R.drawable.pause_button)
                manageTimerByPlayerState(playerState)
            }

            PlayerState.PAUSED -> {
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                manageTimerByPlayerState(playerState)
            }

            PlayerState.DEFAULT -> {
                binding.ivPlayButton.isEnabled = false
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                manageTimerByPlayerState(playerState)
            }
        }
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
                        binding.tvTrackTimer.text = viewModel.getCurrentTimerPosition()
                        playerActivityHandler.postDelayed(this, TIMER_DELAY)
                    }
                }

                playerActivityHandler.postDelayed(runnable, TIMER_DELAY)
                timerRunnable = runnable
            }

            PlayerState.PAUSED -> timerRunnable?.let { playerActivityHandler.removeCallbacksAndMessages(timerRunnable) }
        }
    }

    private fun setButtonState(actionView: View, state: Boolean) {
        when {
            (actionView == binding.ivAddToPlaylistButton) -> (actionView as? ImageView)
                ?.setImageResource(if (state) R.drawable.added_in_playlist else R.drawable.add_to_playlist)

            (actionView == binding.ivAddToFavoritesButton) -> (actionView as? ImageView)
                ?.setImageResource(if (state) R.drawable.added_in_favorites else R.drawable.add_to_favorite)
        }
    }

    companion object {
        private const val ZERO_TIMER = "00:00"
        private const val VALUE_TIMER = "timer_value"
        private const val TIMER_DELAY = 400L

        private const val IN_PLAYLIST_VALUE = "IN_PLAYLIST_VALUE"
        private const val IN_FAVORITE_VALUE = "IN_FAVORITE_VALUE"
    }
}

