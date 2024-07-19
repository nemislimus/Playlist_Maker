package com.practicum.playlistmaker.ui.mediateka

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding

class MediatekaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediatekaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}