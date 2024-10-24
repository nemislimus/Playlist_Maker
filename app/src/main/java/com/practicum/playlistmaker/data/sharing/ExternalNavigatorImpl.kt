package com.practicum.playlistmaker.data.sharing

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.api.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun getStringResourceById(id: Int): String {
        return context.getString(id)
    }

    override fun shareMessageOrLink(messageOrLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, messageOrLink)
        }

        val shareChooser = Intent.createChooser(shareIntent, context.getString(R.string.where_send_link))
        shareChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(context, shareChooser, Bundle())
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.no_apps_for_text_sending),
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
                context.getString(R.string.no_browser_app),
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
                context.getString(R.string.no_apps_for_email),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}