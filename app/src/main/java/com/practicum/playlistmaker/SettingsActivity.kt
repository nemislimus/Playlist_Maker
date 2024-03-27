package com.practicum.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val outOfSettingsButton = findViewById<Button>(R.id.outOfSettingsButton)
        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val supportButton = findViewById<Button>(R.id.supportButton)
        val userAgreementButton = findViewById<Button>(R.id.userAgreementButton)

        //Выход с экрана настроек
        outOfSettingsButton.setOnClickListener {
            finish()
        }

//        // Просто по приколу для себя поискал и описал переключатель темы
//        switchTheme.setOnCheckedChangeListener { _, isChecked ->
//
//            if (isChecked) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//            recreate()
//        }

        // Шарим ссылку во все возможные приложения
        shareButton.setOnClickListener {
            val linkMessage = getString(R.string.link_share_android_dev)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, linkMessage)
            }

            val shareChooser = Intent.createChooser(shareIntent, "Укажите куда отправить ссылку:")

            try {
                startActivity(shareChooser)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    "На устройстве нет приложений для отправки текста!",
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

//            supportIntent.data = Uri.parse("mailto:$email?subject=$messageTopic&body=$message")

            try {
                startActivity(supportIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    "На устройстве нет почтового клиента!",
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
                    "На устройстве нет браузера!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}