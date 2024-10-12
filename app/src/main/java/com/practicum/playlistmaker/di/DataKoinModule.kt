package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.data.db.AppDataBase
import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.data.search.NetworkClient
import com.practicum.playlistmaker.data.search.TracksStorage
import com.practicum.playlistmaker.data.search.network.Retrofit
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.search.storage.SharedPrefsTracksStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    /////////////////////////////////// Search block
    single<NetworkClient> {
        RetrofitNetworkClient(get(), Retrofit.itunesInstance)
    }

    single<TracksStorage> {
        SharedPrefsTracksStorage(
            androidContext().getSharedPreferences(
                SharedPrefsTracksStorage.TRACK_STORAGE,
                Context.MODE_PRIVATE
            ),
            get()
        )
    }

    factory {
        Gson()
    }

    /////////////////////////////////// Player block
    factory {
        MediaPlayer()
    }

    /////////////////////////////////// AppDatabase block
    single<AppDataBase> {
        AppDataBase.getAppDatabase(get())
    }

    single<FavoriteTrackDao> {
        get<AppDataBase>().favoriteTrackDao()
    }

    single<PlaylistsDao> {
        get<AppDataBase>().playlistDao()
    }
}
