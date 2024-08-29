package com.practicum.playlistmaker.ui.mediateka.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class FavoriteTracksFragmentViewModel(
    private val noRealData: Boolean,
): ViewModel() {

    private val favoriteLiveData = MutableLiveData(noRealData)
    fun getFavoriteLiveData(): LiveData<Boolean> = favoriteLiveData

}