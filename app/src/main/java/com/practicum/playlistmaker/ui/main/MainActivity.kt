package com.practicum.playlistmaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.ui.mediateka.MediatekaActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.ui.search.activity.SearchActivity
import com.practicum.playlistmaker.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            val clickOnSearchButton = Intent(this, SearchActivity::class.java)
            startActivity(clickOnSearchButton)
        }

        binding.mediaButton.setOnClickListener {
            val clickOnMediatekaButton = Intent(this, MediatekaActivity::class.java)
            startActivity(clickOnMediatekaButton)
        }

        binding.settingsButton.setOnClickListener {
            val clickOnSettingsButton = Intent(this, SettingsActivity::class.java)
            startActivity(clickOnSettingsButton)
        }


    }
}