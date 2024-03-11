package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainScreenButton = findViewById<Button>(R.id.BackFromSettingsButton)

        backToMainScreenButton.setOnClickListener {
            val clickOnBackButton = Intent(this, MainActivity::class.java)
            startActivity(clickOnBackButton)
        }

    }
}