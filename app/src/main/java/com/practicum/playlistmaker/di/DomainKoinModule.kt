package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
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
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {

    // Search block
    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    // Player block
    factory<PlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    factory<PlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    // Settings-Sharing block
    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(
            androidContext().getSharedPreferences(
                SettingsRepository.SETTINGS_STORAGE,
                Context.MODE_PRIVATE
            )
        )
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImlp(androidContext())
    }
}