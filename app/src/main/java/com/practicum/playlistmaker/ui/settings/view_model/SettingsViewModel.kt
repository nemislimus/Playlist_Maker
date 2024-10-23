package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.models.ThemeSettings
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private var themeDarkModeValueLiveData =
        MutableLiveData(getDarkModeValue())

    fun observeDarkModeValueLiveData(): LiveData<Boolean> = themeDarkModeValueLiveData

    private fun getDarkModeValue(): Boolean {
        return settingsInteractor.getThemeSettings().nightModeOn
    }

    fun updateDarkModeValue(value: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(value))
        themeDarkModeValueLiveData.postValue(value)
    }


    fun shareAppMessageOrLink(linkOrMessageResourceId: Int) {
        sharingInteractor.shareAppMessageOrLink(linkOrMessageResourceId)
    }

    fun openTerms(linkResourceId: Int) {
        sharingInteractor.openTerms(linkResourceId)
    }

    fun openSupportEmail(
        emailAddressResourceId: Int,
        messageTopicResourceId: Int,
        messageResourceId: Int,
    ) {
        sharingInteractor.openSupport(
            emailAddressResourceId = emailAddressResourceId,
            messageTopicResourceId = messageTopicResourceId,
            messageResourceId = messageResourceId
        )
    }

}