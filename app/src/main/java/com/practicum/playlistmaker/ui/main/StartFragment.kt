package com.practicum.playlistmaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentStartBinding
import com.practicum.playlistmaker.ui.mediateka.fragments.MediatekaFragment
import com.practicum.playlistmaker.ui.search.fragments.SearchFragment
import com.practicum.playlistmaker.ui.settings.fragments.SettingsFragment

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {

            parentFragmentManager.commit {
                replace(
                    R.id.mainContainerView,
                    SearchFragment(),
                    SearchFragment.TAG
                )

                addToBackStack(SearchFragment.TAG)
            }
        }

        binding.mediaButton.setOnClickListener {

            parentFragmentManager.commit {
                replace(
                    R.id.mainContainerView,
                    MediatekaFragment(),
                    MediatekaFragment.TAG
                )

                addToBackStack(MediatekaFragment.TAG)
            }
        }

        binding.settingsButton.setOnClickListener {

            parentFragmentManager.commit {
                replace(
                    R.id.mainContainerView,
                    SettingsFragment(),
                    SettingsFragment.TAG
                )

                addToBackStack(SettingsFragment.TAG)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "start_tag"
    }
}