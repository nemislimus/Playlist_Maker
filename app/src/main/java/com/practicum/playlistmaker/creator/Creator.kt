package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.impl.MediaPlayaerInteractorImpl

object Creator {
    private fun getMediaPlayerRepository(): PlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    fun provideMediaPlayaerInteractor(): PlayerInteractor {
        return MediaPlayaerInteractorImpl(getMediaPlayerRepository())
    }
}