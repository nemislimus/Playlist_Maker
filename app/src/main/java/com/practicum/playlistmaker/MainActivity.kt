package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainSearchButton = findViewById<Button>(R.id.searchButton)
        val mainMediatekaButton = findViewById<Button>(R.id.mediaButton)
        val mainSettingsButton = findViewById<Button>(R.id.settingsButton)

        mainSearchButton.setOnClickListener {
            val clickOnSearchButton = Intent(this, SearchActivity::class.java)
            startActivity(clickOnSearchButton)
        }

        mainMediatekaButton.setOnClickListener {
            val clickOnMediatekaButton = Intent(this, MediatekaActivity::class.java)
            startActivity(clickOnMediatekaButton)
        }

        mainSettingsButton.setOnClickListener {
            val clickOnSettingsButton = Intent(this, SettingsActivity::class.java)
            startActivity(clickOnSettingsButton)
        }


    }
}