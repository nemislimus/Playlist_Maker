package com.practicum.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.ui.mediateka.view_models.FavoriteTracksFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteTracksFragment: Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteTracksFragmentViewModel by viewModel<FavoriteTracksFragmentViewModel> {
        parametersOf(requireArguments().getBoolean(NO_DATA))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteTracksFragmentViewModel.getFavoriteLiveData().observe(viewLifecycleOwner) { noData ->
            binding.favoritesPlaceholderGroup.isVisible = noData
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        private const val NO_DATA = "no_data"

        fun newInstance(noData: Boolean) = FavoriteTracksFragment().apply {
            arguments = bundleOf(NO_DATA to noData)
        }
    }
}