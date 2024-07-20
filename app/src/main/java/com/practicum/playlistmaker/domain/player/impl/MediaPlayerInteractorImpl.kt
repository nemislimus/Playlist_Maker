package com.practicum.playlistmaker.domain.player.impl


import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.api.PlayerRepository
import com.practicum.playlistmaker.ui.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.models.Track

class MediaPlayerInteractorImpl(
    private val playerRepository: PlayerRepository
): PlayerInteractor {
    override fun prepare(listener: PlayerInteractor.OnStateChangeListener) {
        playerRepository.preparePlayer(listener)
    }

    override fun start() {
        playerRepository.startPlayer()
    }

    override fun pause() {
        playerRepository.pausePlayer()
    }

    override fun release() {
        playerRepository.releasePlayer()
    }

    override fun getPlayerCurrentTimerPosition(): String {
        return playerRepository.getPlayerCurrentTimerPosition()
    }

    override fun getPlayerState(): PlayerState {
        return playerRepository.getPlayerState()
    }

    override fun setPlayerDataSource(track: Track) {
        playerRepository.setPlayerDataSource(track)
    }
}