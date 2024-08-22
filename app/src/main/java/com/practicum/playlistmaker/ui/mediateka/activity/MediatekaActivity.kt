package com.practicum.playlistmaker.ui.mediateka.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding
import com.practicum.playlistmaker.ui.mediateka.fragments.MediatekaFragmentsAdapter

class MediatekaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding
    private lateinit var mediatekaTabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mediatekaOutButton.setOnClickListener {
            finish()
        }

        binding.mediatekaViewPager.adapter = MediatekaFragmentsAdapter(supportFragmentManager, lifecycle)

        mediatekaTabMediator = TabLayoutMediator(
            binding.mediatekaTabLayout,
            binding.mediatekaViewPager
        ) { tabElement, position ->
            when(position) {
                0 -> tabElement.text = getString(R.string.favorite_tracks)
                1 -> tabElement.text = getString(R.string.playlists)
            }
        }

        mediatekaTabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediatekaTabMediator.detach()
    }
}