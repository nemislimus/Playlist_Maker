package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FavoriteTracksFragmentViewModel(
    private val favoriteInteractor: FavoriteTracksInteractor,
): ViewModel() {

    private val _favoriteTracks = MutableStateFlow(emptyList<Track>())
    fun favoriteTracks() = _favoriteTracks.asStateFlow()

    fun getFavoriteTracks() {
        viewModelScope.launch { //this: CoroutineScope
            favoriteInteractor.getAllFavoriteTracks().flowOn(Dispatchers.IO).collect {
                tracksFromDb -> _favoriteTracks.update { tracksFromDb }
            }
        }
    }
}