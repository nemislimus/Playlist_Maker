package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.storage.SharedPrefsTracksStorage
import com.practicum.playlistmaker.data.settings.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImlp
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.api.PlayerRepository
import com.practicum.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.api.TracksRepository
import com.practicum.playlistmaker.domain.search.imp.TracksInteractorImpl
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.api.SettingsRepository
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.api.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImlp(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(context),
            tracksStorage = SharedPrefsTracksStorage(context)
        )
    }

    fun provideMediaPlayerInteractor(): PlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getMediaPlayerRepository(): PlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

}