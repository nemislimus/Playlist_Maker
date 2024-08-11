package com.practicum.playlistmaker.data.player

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.player.api.PlayerRepository
import com.practicum.playlistmaker.domain.player.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerRepositoryImpl(
    private val player: MediaPlayer
): PlayerRepository {
    private var playerState: PlayerState = PlayerState.DEFAULT
    private lateinit var listener: PlayerRepository.OnStateChangeListener

    override fun preparePlayer(listener: PlayerRepository.OnStateChangeListener) {
        this.listener = listener
        player.prepareAsync()

        player.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            listener.onChange()
        }

        player.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            listener.onChange()
        }
    }

    override fun startPlayer() {
        player.start()
        playerState = PlayerState.PLAYING
        listener.onChange()
    }

    override fun pausePlayer() {
        player.pause()
        playerState = PlayerState.PAUSED
        listener.onChange()
    }

    override fun releasePlayer() {
        playerState = PlayerState.DEFAULT
        player.release()
    }

    override fun getPlayerCurrentTimerPosition(): String {
       return SimpleDateFormat("mm:ss", Locale.getDefault())
           .format(player.currentPosition.toLong())
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun setPlayerDataSource(track: Track) {
        player.setDataSource(track.previewUrl)
    }
}