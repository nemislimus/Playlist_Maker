package com.practicum.playlistmaker.domain.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.api.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class SharingInteractorImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.link_share_android_dev)
    }

    private fun getSupportEmailData(): EmailData {
        return CONST_EMAIL_DATA
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.link_userAgreementButton)
    }

    companion object {

        val CONST_EMAIL_DATA =
            EmailData(
                emailAddress = "nemislimus@yandex.ru",
                messageTopic = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
                message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            )
    }

}