package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsFragmentViewModel(
    private val noRealData: Boolean,
): ViewModel() {

    private val playlistsLiveData = MutableLiveData(noRealData)
    fun getPlaylistsLiveData(): LiveData<Boolean> = playlistsLiveData

}