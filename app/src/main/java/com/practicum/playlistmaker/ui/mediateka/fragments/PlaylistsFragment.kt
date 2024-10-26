package com.practicum.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.mediateka.PlaylistsAdapter
import com.practicum.playlistmaker.ui.mediateka.models.PlaylistsState
import com.practicum.playlistmaker.ui.mediateka.view_models.PlaylistsFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistsAdapter = PlaylistsAdapter(isPlayerPlaylist = false) { playlist ->
        findNavController().navigate(
            R.id.action_mediatekaFragment_to_playlistInsideFragment,
            bundleOf(PlaylistInsideFragment.PLAYLIST_KEY to  playlist.id)
        )
    }

    private val playlistsFragmentViewModel by viewModel<PlaylistsFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure RecyclerView
        binding.rvPlaylists.adapter = playlistsAdapter

        // Observe playlists StateFlow
        showPlaylists()

        binding.createNewPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediatekaFragment_to_newPlaylistFragment,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsFragmentViewModel.getPlaylists()
    }

    private fun showPlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistsFragmentViewModel.playlists().collect { playlistsState ->
                renderState(playlistsState)
            }
        }
    }

    private fun renderState(playlistsState: PlaylistsState) {
        when(playlistsState) {
            is PlaylistsState.Loading -> showNoting()
            is PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(playlistsState.playlists)
        }
    }

    private fun showNoting() {
        binding.rvPlaylists.isVisible = false
        binding.playlistsPlaceholderGroup.isVisible = false
    }

    private fun showPlaceholder() {
        binding.rvPlaylists.isVisible = false
        binding.playlistsPlaceholderGroup.isVisible = true
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.rvPlaylists.isVisible = true
        binding.playlistsPlaceholderGroup.isVisible = false
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}