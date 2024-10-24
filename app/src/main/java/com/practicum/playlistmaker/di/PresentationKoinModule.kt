package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.mediateka.view_models.FavoriteTracksFragmentViewModel
import com.practicum.playlistmaker.ui.mediateka.view_models.NewPlaylistFragmentViewModel
import com.practicum.playlistmaker.ui.mediateka.view_models.PlaylistInsideFragmentViewModel
import com.practicum.playlistmaker.ui.mediateka.view_models.PlaylistsFragmentViewModel
import com.practicum.playlistmaker.ui.mediateka.view_models.UpdatePlaylistFragmentViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    /////////////////////////////////// Search block
    viewModel {
        TracksViewModel(get(), get(), get())
    }

    /////////////////////////////////// Player block
    viewModel { (track: Track) ->
        PlayerViewModel(get(), get(), track, get(), get())
    }

    /////////////////////////////////// Settings-Sharing block
    viewModel {
        SettingsViewModel(get(), get())
    }

    /////////////////////////////////// Mediateka block
    viewModel {
        FavoriteTracksFragmentViewModel(get())
    }

    viewModel {
        PlaylistsFragmentViewModel(get())
    }

    viewModel {
        NewPlaylistFragmentViewModel(get())
    }

    viewModel {(playlistId: Long) ->
        UpdatePlaylistFragmentViewModel(get(), playlistId)
    }

    viewModel { (playlistId: Long) ->
        PlaylistInsideFragmentViewModel(playlistId, get(), get())
    }
}