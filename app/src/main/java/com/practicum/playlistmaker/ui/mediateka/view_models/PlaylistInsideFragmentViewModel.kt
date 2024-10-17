package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.mediateka.models.PlaylistsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistInsideFragmentViewModel(

): ViewModel() {

//    private val _playlists = MutableStateFlow<PlaylistsState>(PlaylistsState.Loading)
//    fun playlists() = _playlists.asStateFlow()
//
//    fun getPlaylists() {
//        viewModelScope.launch { //this: CoroutineScope
//            playlistsInteractor.getAllPlaylists().collect {
//                    playlistFromDb -> consumeDbData(playlistFromDb)
//            }
//        }
//    }
//
//    private fun consumeDbData(data: List<Playlist>) {
//        if (data.isNotEmpty()) {
//            _playlists.update { PlaylistsState.Content(data) }
//        } else {
//            _playlists.update { PlaylistsState.Empty }
//        }
//    }
}