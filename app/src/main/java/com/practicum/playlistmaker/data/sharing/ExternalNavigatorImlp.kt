package com.practicum.playlistmaker.data.sharing

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.practicum.playlistmaker.domain.sharing.api.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class ExternalNavigatorImlp(private val context: Context) : ExternalNavigator {

    override fun shareLink(shareLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareLink)
        }

        val shareChooser = Intent.createChooser(shareIntent, WHERE_SEND_LINK)
        shareChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(context, shareChooser, Bundle())
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                NO_APPS_FOR_TEXT_SENDING,
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun openLink(termsLink: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
        agreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(context, agreementIntent, Bundle())
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                NO_APPS_FOR_BROWSE,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun openEmail(email: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, email.messageTopic)
        supportIntent.putExtra(Intent.EXTRA_TEXT, email.message)
        supportIntent.putExtra(Intent.EXTRA_EMAIL, email.emailAddress)

        try {
            startActivity(context, supportIntent, Bundle())
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                NO_APPS_FOR_EMAIL,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object{
        const val WHERE_SEND_LINK = "Укажите куда отправить ссылку:"
        const val NO_APPS_FOR_TEXT_SENDING = "На устройстве нет приложений для отправки текста!"
        const val NO_APPS_FOR_BROWSE = "На устройстве нет браузера!"
        const val NO_APPS_FOR_EMAIL = "На устройстве нет почтового клиента!"
    }
}