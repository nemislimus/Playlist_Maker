package com.practicum.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsSharePrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        val outOfSettingsButton = findViewById<Button>(R.id.outOfSettingsButton)
        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val supportButton = findViewById<Button>(R.id.supportButton)
        val userAgreementButton = findViewById<Button>(R.id.userAgreementButton)

        switchTheme.isChecked = (applicationContext as PlaylistApp).darkThemeValue

        //Выход с экрана настроек
        outOfSettingsButton.setOnClickListener {
            finish()
        }

        // Реализуем переключатель темы
        switchTheme.setOnCheckedChangeListener { _, checked ->
            settingsSharePrefs.edit()
                .putBoolean(THEME_KEY, checked)
                .apply()

            (applicationContext as PlaylistApp).switchTheme(checked)
        }

        // Шарим ссылку во все возможные приложения
        shareButton.setOnClickListener {
            val linkMessage = getString(R.string.link_share_android_dev)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, linkMessage)
            }

            val shareChooser = Intent.createChooser(shareIntent, getString(R.string.where_send_link))

            try {
                startActivity(shareChooser)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.no_apps_for_text_sending),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Пишем письмо в поддержку
        supportButton.setOnClickListener {
            val message = getString(R.string.support_message_text)
            val messageTopic = getString(R.string.support_message_topic)
            val email = arrayOf(getString(R.string.support_email))

            val supportIntent = Intent(Intent.ACTION_SENDTO)

            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, messageTopic)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.putExtra(Intent.EXTRA_EMAIL, email)

        try {
                startActivity(supportIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.no_apps_for_email),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Открываем пользовательское соглашение
        userAgreementButton.setOnClickListener {
            val agreementLink = Uri.parse(getString(R.string.link_userAgreementButton))
            val agreementIntent = Intent(Intent.ACTION_VIEW, agreementLink)

            try {
                startActivity(agreementIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.no_browser_app),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}