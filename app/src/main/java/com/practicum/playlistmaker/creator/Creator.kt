package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.storage.SharedPrefsTracksStorage
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.api.PlayerRepository
import com.practicum.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.api.TracksRepository
import com.practicum.playlistmaker.domain.search.imp.TracksInteractorImpl

object Creator {

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideMediaPlayerInteractor(): PlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(context),
            tracksStorage = SharedPrefsTracksStorage(context)
        )
    }

    private fun getMediaPlayerRepository(): PlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

}