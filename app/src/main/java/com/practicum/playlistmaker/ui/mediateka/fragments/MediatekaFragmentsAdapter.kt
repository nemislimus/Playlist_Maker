package com.practicum.playlistmaker.ui.mediateka.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediatekaFragmentsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val noData: Boolean = true,
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance(noData = noData)
        }
    }
}