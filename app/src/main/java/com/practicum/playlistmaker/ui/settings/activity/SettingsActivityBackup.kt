package com.practicum.playlistmaker.ui.settings.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivityBackup : AppCompatActivity() {
//    private lateinit var binding: ActivitySettingsBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val settingsSharePrefs = getSharedPreferences(PlaylistApp.APP_PREFERENCES, MODE_PRIVATE)
//
//
//        binding.switchTheme.isChecked = (applicationContext as PlaylistApp).darkThemeValue
//
//        // Exit from Settings
//        binding.outOfSettingsButton.setOnClickListener {
//            finish()
//        }
//
//        // Handle theme switch
//        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
//            settingsSharePrefs.edit()
//                .putBoolean(PlaylistApp.THEME_KEY, checked)
//                .apply()
//
//            (applicationContext as PlaylistApp).switchTheme(checked)
//        }
//
//        // Share
//        binding.shareButton.setOnClickListener {
//            val linkMessage = getString(R.string.link_share_android_dev)
//            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                type = "text/plain"
//                putExtra(Intent.EXTRA_TEXT, linkMessage)
//            }
//
//            val shareChooser = Intent.createChooser(shareIntent, getString(R.string.where_send_link))
//
//            try {
//                startActivity(shareChooser)
//            } catch (_: ActivityNotFoundException) {
//                Toast.makeText(
//                    this@SettingsActivity,
//                    getString(R.string.no_apps_for_text_sending),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//
//        // Email to support
//        binding.supportButton.setOnClickListener {
//            val message = getString(R.string.support_message_text)
//            val messageTopic = getString(R.string.support_message_topic)
//            val email = arrayOf(getString(R.string.support_email))
//
//            val supportIntent = Intent(Intent.ACTION_SENDTO)
//
//            supportIntent.data = Uri.parse("mailto:")
//            supportIntent.putExtra(Intent.EXTRA_SUBJECT, messageTopic)
//            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
//            supportIntent.putExtra(Intent.EXTRA_EMAIL, email)
//
//            try {
//                startActivity(supportIntent)
//            } catch (_: ActivityNotFoundException) {
//                Toast.makeText(
//                    this@SettingsActivity,
//                    getString(R.string.no_apps_for_email),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//
//        // Opening the user agreement
//        binding.userAgreementButton.setOnClickListener {
//            val agreementLink = Uri.parse(getString(R.string.link_userAgreementButton))
//            val agreementIntent = Intent(Intent.ACTION_VIEW, agreementLink)
//
//            try {
//                startActivity(agreementIntent)
//            } catch (_: ActivityNotFoundException) {
//                Toast.makeText(
//                    this@SettingsActivity,
//                    getString(R.string.no_browser_app),
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
}