package com.practicum.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.createJsonFromTrack
import com.practicum.playlistmaker.ui.mediateka.FavoriteTracksAdapter
import com.practicum.playlistmaker.ui.mediateka.view_models.FavoriteTracksFragmentViewModel
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private var isListItemClickAllowed: Boolean = true

    private val favoriteTracksAdapter: FavoriteTracksAdapter = FavoriteTracksAdapter { track ->
        if (clickListItemDebounce()) manageOnFavoriteClick(track)
    }


    private val favoriteFragmentViewModel by viewModel<FavoriteTracksFragmentViewModel>()

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

        // Configure RecyclerView
        binding.rvFavoriteTrackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFavoriteTrackList.adapter = favoriteTracksAdapter

        // Observe StateFlow (like as LiveData)
        showFavoriteTracks()
    }

    override fun onResume() {
        super.onResume()
        favoriteFragmentViewModel.getFavoriteTracks()
    }

    private fun showFavoriteTracks() {
        viewLifecycleOwner.lifecycleScope.launch {
            favoriteFragmentViewModel.favoriteTracks().collect { tracks ->
                render(tracks)
            }
        }
    }

    private fun render(tracks: List<Track>) {
        if (tracks.isNotEmpty()) {
            binding.rvFavoriteTrackList.isVisible = true
            binding.favoritesPlaceholderGroup.isVisible = false
        } else {
            binding.rvFavoriteTrackList.isVisible = false
            binding.favoritesPlaceholderGroup.isVisible = true
        }
        favoriteTracksAdapter.favoriteTracks.clear()
        favoriteTracksAdapter.favoriteTracks.addAll(tracks)
        favoriteTracksAdapter.notifyDataSetChanged()
    }

    private fun manageOnFavoriteClick(track: Track) {
        findNavController().navigate(
            R.id.action_mediatekaFragment_to_playerActivity,
            PlayerActivity.createArgs(createJsonFromTrack(track))
        )
    }

    private fun clickListItemDebounce(): Boolean {
        val current = isListItemClickAllowed
        if (isListItemClickAllowed) {
            isListItemClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isListItemClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1500L

        fun newInstance() = FavoriteTracksFragment()
    }
}