package com.practicum.playlistmaker.ui.settings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        viewModel.observeDarkModeValueLiveData().observe(this){
            binding.switchTheme.isChecked = (applicationContext as PlaylistApp).darkThemeValue
        }

        // Exit from Settings
        binding.outOfSettingsButton.setOnClickListener {
            finish()
        }

        // Handle theme switch
        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.updateDarkModeValue(checked)
            (applicationContext as PlaylistApp).switchTheme(checked)
        }

        // Share
        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        // Email to support
        binding.supportButton.setOnClickListener {
            viewModel.openSupportEmail()

        }

        // Opening the user agreement
        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }
    }
}