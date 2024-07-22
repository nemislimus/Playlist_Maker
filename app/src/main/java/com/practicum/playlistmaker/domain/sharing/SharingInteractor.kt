package com.practicum.playlistmaker.domain.sharing

interface SharingInteractor {
    fun shareApp(linkResourceId: Int)
    fun openTerms(linkResourceId: Int)
    fun openSupport(
        emailAddressResourceId: Int,
        messageTopicResourceId: Int,
        messageResourceId: Int,
    )
}