package com.practicum.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.ui.mediateka.view_models.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistsFragment: Fragment() {

    private lateinit var binding: FragmentPlaylistBinding

    private val playlistsFragmentViewModel by viewModel<PlaylistsFragmentViewModel> {
        parametersOf(requireArguments().getBoolean(NO_DATA))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsFragmentViewModel.getPlaylistsLiveData().observe(viewLifecycleOwner) { noData ->
            binding.playlistsPlaceholderGroup.isVisible = noData
        }
    }

    companion object {

        private const val NO_DATA = "no_data"

        fun newInstance(noData: Boolean) = PlaylistsFragment().apply {
            arguments = bundleOf(NO_DATA to noData)
        }
    }
}