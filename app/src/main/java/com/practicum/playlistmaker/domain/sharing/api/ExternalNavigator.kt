package com.practicum.playlistmaker.domain.sharing.api

import com.practicum.playlistmaker.domain.sharing.models.EmailData

interface ExternalNavigator {

    fun shareMessageOrLink(messageOrLink: String)

    fun openLink(termsLink: String)

    fun openEmail(email: EmailData)

    fun getStringResourceById(id: Int): String
}