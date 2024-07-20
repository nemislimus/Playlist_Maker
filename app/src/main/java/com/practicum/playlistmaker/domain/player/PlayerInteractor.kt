package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.ui.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.models.Track

interface PlayerInteractor {
    fun prepare(listener: OnStateChangeListener)
    fun start()
    fun pause()
    fun release()
    fun getPlayerCurrentTimerPosition(): String
    fun getPlayerState(): PlayerState
    fun setPlayerDataSource(track: Track)

    interface OnStateChangeListener {
        fun onChange()
    }
}