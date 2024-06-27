package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.entities.PlayerState
import com.practicum.playlistmaker.domain.entities.Track

interface PlayerRepository {
    fun preparePlayer(listener: PlayerInteractor.OnStateChangeListener)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerCurrentTimerPosition(): String
    fun getPlayerState(): PlayerState
    fun setPlayerDataSource(track: Track)
}