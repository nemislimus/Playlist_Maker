package com.practicum.playlistmaker.domain.sharing.impl

import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.api.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp(linkResourceId: Int) {
        externalNavigator.shareLink(getShareAppLink(linkResourceId))
    }

    override fun openTerms(linkResourceId: Int) {
        externalNavigator.openLink(getTermsLink(linkResourceId))
    }

    override fun openSupport(
        emailAddressResourceId: Int,
        messageTopicResourceId: Int,
        messageResourceId: Int,
    ) {
        externalNavigator.openEmail(
            getSupportEmailData(
                emailAddressResourceId,
                messageTopicResourceId,
                messageResourceId
            )
        )
    }

    private fun getShareAppLink(resourceId: Int): String {
        return externalNavigator.getStringResourceById(resourceId)
    }

    private fun getSupportEmailData(
        emailAddressResourceId: Int,
        messageTopicResourceId: Int,
        messageResourceId: Int,
    ): EmailData {
        return EmailData(
            emailAddress = externalNavigator.getStringResourceById(emailAddressResourceId),
            messageTopic = externalNavigator.getStringResourceById(messageTopicResourceId),
            message = externalNavigator.getStringResourceById(messageResourceId)
        )
    }

    private fun getTermsLink(resourceId: Int): String {
        return externalNavigator.getStringResourceById(resourceId)
    }
}