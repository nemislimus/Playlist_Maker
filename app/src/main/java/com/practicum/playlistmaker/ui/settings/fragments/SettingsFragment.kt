package com.practicum.playlistmaker.ui.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeDarkModeValueLiveData().observe(viewLifecycleOwner){
            binding.switchTheme.isChecked = (requireActivity().applicationContext as PlaylistApp).darkThemeValue
        }

        // Handle theme switch
        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.updateDarkModeValue(checked)
            (requireActivity().applicationContext as PlaylistApp).switchTheme(checked)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}