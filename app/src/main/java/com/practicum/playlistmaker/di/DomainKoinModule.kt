package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.data.mediateka.favorite.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.data.mediateka.playlists.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.TracksRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksRepository
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.api.PlaylistsRepository
import com.practicum.playlistmaker.domain.db.impl.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.domain.db.impl.PlaylistsInteractorImpl
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

    /////////////////////////////////// Search block
    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    /////////////////////////////////// Player block
    factory<PlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    factory<PlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    /////////////////////////////////// Settings-Sharing block
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
        ExternalNavigatorImpl(get())
    }

    /////////////////////////////////// AppDatabase block

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }

    factory<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }

    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

}