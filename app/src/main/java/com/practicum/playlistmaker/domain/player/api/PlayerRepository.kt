package com.practicum.playlistmaker.domain.player.api

import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.models.Track

interface PlayerRepository {
    fun preparePlayer(listener: PlayerInteractor.OnStateChangeListener)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerCurrentTimerPosition(): String
    fun getPlayerState(): PlayerState
    fun setPlayerDataSource(track: Track)
}