package com.practicum.playlistmaker.ui.mediateka.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding
import com.practicum.playlistmaker.ui.mediateka.fragments.MediatekaFragmentsAdapter

class MediatekaFragment : Fragment() {

    private var _binding: FragmentMediatekaBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediatekaTabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mediatekaOutButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.mediatekaViewPager.adapter =
            MediatekaFragmentsAdapter(childFragmentManager, lifecycle)

        mediatekaTabMediator = TabLayoutMediator(
            binding.mediatekaTabLayout,
            binding.mediatekaViewPager
        ) { tabElement, position ->
            when (position) {
                0 -> tabElement.text = getString(R.string.favorite_tracks)
                1 -> tabElement.text = getString(R.string.playlists)
            }
        }

        mediatekaTabMediator.attach()
    }

    override fun onDestroyView() {
        _binding = null
        mediatekaTabMediator.detach()
        super.onDestroyView()
    }

    companion object {
        const val TAG = "mediateka_tag"
    }
}