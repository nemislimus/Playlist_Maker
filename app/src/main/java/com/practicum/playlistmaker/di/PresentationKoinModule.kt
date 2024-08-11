package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    // Search block
    viewModel {
        TracksViewModel(get(),androidContext())
    }

    // Player block
    viewModel { (track: Track) ->
        PlayerViewModel(get(), track, androidContext())
    }

    // Settings-Sharing block
    viewModel {
        SettingsViewModel(get(), get())
    }
}