package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.entities.PlayerState
import com.practicum.playlistmaker.domain.entities.Track

interface PlayerInteractor {
    fun prepare(listener: OnStateChangeListener)
    fun start()
    fun pause()
    fun release()
    fun getPlayerCurrentTimerPosition(): String
    fun getPlayerState(): PlayerState
    fun setPlayerDataSource(track: Track)

    interface OnStateChangeListener {
        fun onChange(state: PlayerState)
    }
}