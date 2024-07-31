package com.practicum.playlistmaker.ui.settings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            viewModel.shareApp(R.string.link_share_android_dev)
        }

        // Email to support
        binding.supportButton.setOnClickListener {
            viewModel.openSupportEmail(
                emailAddressResourceId = R.string.support_email,
                messageTopicResourceId = R.string.support_message_topic,
                messageResourceId = R.string.support_message_text
            )
        }

        // Opening the user agreement
        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms(R.string.link_userAgreementButton)
        }
    }
}