package com.practicum.playlistmaker.domain.sharing

interface SharingInteractor {

    fun shareAppMessageOrLink(linkOrMessageResourceId: Int)

    fun sharePlaylist(playlistInfo: String)

    fun openTerms(linkResourceId: Int)

    fun openSupport(
        emailAddressResourceId: Int,
        messageTopicResourceId: Int,
        messageResourceId: Int,
    )
}