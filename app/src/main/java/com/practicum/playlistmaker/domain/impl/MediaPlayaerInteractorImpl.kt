package com.practicum.playlistmaker.domain.impl


import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.entities.PlayerState
import com.practicum.playlistmaker.domain.entities.Track

class MediaPlayaerInteractorImpl(
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