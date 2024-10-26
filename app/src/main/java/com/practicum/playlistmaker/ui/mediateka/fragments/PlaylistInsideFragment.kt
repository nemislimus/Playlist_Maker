package com.practicum.playlistmaker.ui.mediateka.fragments

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistInsideBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.createJsonFromTrack
import com.practicum.playlistmaker.ui.dpToPx
import com.practicum.playlistmaker.ui.mediateka.view_models.PlaylistInsideFragmentViewModel
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInsideFragment: Fragment() {

    private lateinit var currentPlaylist: Playlist
    private var isTrackListItemClickAllowed: Boolean = true

    private var _binding: FragmentPlaylistInsideBinding? = null
    private val binding get() = _binding!!

    private val cumulativeTimeFormater by lazy { SimpleDateFormat("mm", Locale.getDefault()) }
    private val trackTimeFormater by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var trackListBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var trackListBottomSheetIsCollapsedFlag = true

    private var trackAdapter = TrackAdapter(
        { if (clickListItemDebounce()) manageTrackListItemClick(it) },
        { /*NO BUTTON HERE*/ },
        { deleteTrackDialog(it, currentPlaylist).show() }
    )

    private val viewModel by viewModel<PlaylistInsideFragmentViewModel> {
        parametersOf(requireArguments().getLong(PLAYLIST_KEY))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInsideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lock screen rotation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Observe current playlist from database
        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            currentPlaylist = playlist
        }

        // Observe playlist tracks
        showTracks()

        // Configure RecyclerView
        binding.bsTrackList.rvPlaylistTrackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.bsTrackList.rvPlaylistTrackList.adapter = trackAdapter

        trackListBottomSheetBehavior = BottomSheetBehavior.from(binding.bsTrackList.root)

        trackListBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                trackListBottomSheetIsCollapsedFlag =
                    newState != BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.bsMenu.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            // Limit max height by design
            val maxHeight = dpToPx(MAX_MENU_HEIGHT_DP, requireContext())
            val layoutParams = binding.bsMenu.root.layoutParams
            layoutParams.height = maxHeight
            binding.bsMenu.root.layoutParams = layoutParams
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bsTrackList.root.isVisible = slideOffset < 0
            }
        })

        binding.tbPlaylistOutButton.setOnClickListener {
            exitPlaylist()
        }

        binding.ivShareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.ivMenuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.bsMenu.btShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.bsMenu.btDeletePlaylist.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            deletePlaylistDialog().show()
        }

        binding.bsMenu.btEditPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistInsideFragment_to_updatePlaylistFragment,
                UpdatePlaylistFragment.createArgs(currentPlaylist.id)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updatePlaylistData()
    }

    override fun onDestroyView() {
        _binding = null
        // Unlock screen rotation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        super.onDestroyView()
    }

    private fun showTracks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observePlaylistTrackList().collect { tracks ->
                renderPlaylistTracks(tracks)
            }
        }
    }

    private fun updatePlaylistData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val getPlaylistJob = launch(Dispatchers.IO) {
                viewModel.getCurrentPlaylistFromDb()
            }
            getPlaylistJob.join()
            withContext(Dispatchers.IO) {
                viewModel.getPlaylistTrackList(currentPlaylist.trackIdList)
            }
            renderPlaylist(currentPlaylist)

            binding.root.post {
                trackListBottomSheetBehavior.peekHeight = getTrackListBottomSheetCollapseHeight()
                manageTrackListBottomSheetState()
            }
        }
    }

    private fun manageTrackListBottomSheetState() {
        trackListBottomSheetBehavior.state =
            if (trackListBottomSheetIsCollapsedFlag)
                BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    private fun renderPlaylistTracks(trackList: List<Track>) {
        trackAdapter.apply {
            tracks.clear()
            tracks.addAll(trackList)
            notifyDataSetChanged()
        }
    }

    private fun renderPlaylist (playlist: Playlist) {
        showPlaceholder(trackAdapter.tracks.size == 0)

        val trackCountString = "${playlist.tracksCount} ${getCorrectWord(playlist.tracksCount)}"
        val allTracksTime = trackAdapter.tracks.fold(0L) {time, track ->
            time + track.trackTimeMillis
        }.let { cumulativeTimeFormater.format(it) }
        val absoluteTimeString = "$allTracksTime ${requireContext().getString(R.string.min)}"

        with(binding) {
            if (playlist.coverPath.isNotBlank()) {
                ivPlaylistCover.setImageURI(Uri.parse(playlist.coverPath))
            } else {
                ivPlaylistCover.setImageResource(R.drawable.ic_placeholder_track_image)
            }
            tvPlaylistName.text = playlist.playlistName

            if(playlist.playlistDescription.isBlank()) {
                tvPlaylistDescription.isVisible = false
            } else {
                tvPlaylistDescription.text = playlist.playlistDescription
            }
            tvAbsoluteTime.text = absoluteTimeString
            tvTrackCount.text = trackCountString
        }

        // Menu playlist card
        with(binding.bsMenu.PlaylistCard) {
            tvPlaylistName.text = playlist.playlistName
            tvTracksCount.text = trackCountString
            ivPlaylistImage.setImageURI(Uri.parse(playlist.coverPath))
        }
    }

    private fun showPlaceholder(condition: Boolean) {
        if (condition){
            binding.bsTrackList.playlistPlaceholderGroup.isVisible = true
            binding.bsTrackList.rvPlaylistTrackList.isVisible = false
        } else {
            binding.bsTrackList.playlistPlaceholderGroup.isVisible = false
            binding.bsTrackList.rvPlaylistTrackList.isVisible = true
        }
    }

    private fun deletePlaylistDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext(), R.style.DeleteDialog)
            .setTitle(requireContext().getString(R.string.delete_playlist_dialog_title))
            .setMessage(requireContext().getString(R.string.delete_playlist_dialog_message))
            .setNegativeButton(requireContext().getString(R.string.delete_dialog_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(requireContext().getString(R.string.delete_dialog_done)) { _, _ ->
                deletePlaylist()
            }
    }

    private fun deletePlaylist() {
        viewLifecycleOwner.lifecycleScope.launch {
            val deleteAllTracksJob = launch {

                // Clean up all tracks
                trackAdapter.tracks.forEach { track ->
                    val trackJob = launch {
                        val deleteTrackJob = launch(Dispatchers.IO) {
                            viewModel.deleteTrack(track, currentPlaylist)
                        }
                        deleteTrackJob.join()

                        val checkPlaylistJob = launch(Dispatchers.IO) {
                            viewModel.getCurrentPlaylistFromDb()
                        }
                        checkPlaylistJob.join()
                    }
                    trackJob.join()
                }
            }
            deleteAllTracksJob.join()

            // Clean up playlist shell
            val deletePlaylistShellJob = launch {
                viewModel.deletePlaylist(currentPlaylist)
            }
            deletePlaylistShellJob.join()

            exitPlaylist()
        }
    }

    private fun deleteTrackDialog(track: Track, currentPlaylist: Playlist): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext(), R.style.DeleteDialog)
            .setTitle(requireContext().getString(R.string.delete_track_dialog_title))
            .setMessage(requireContext().getString(R.string.delete_track_dialog_message))
            .setNegativeButton(requireContext().getString(R.string.delete_dialog_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(requireContext().getString(R.string.delete_dialog_done)) { _, _ ->
                deleteTrack(track, currentPlaylist)
            }
    }

    private fun deleteTrack(track: Track, playlist: Playlist) {
        viewLifecycleOwner.lifecycleScope.launch {
            val deleteJob = launch(Dispatchers.IO) {
                viewModel.deleteTrack(track, playlist)
            }
            deleteJob.join()
            updatePlaylistData()
        }
    }

    private fun sharePlaylist() {
        if (trackAdapter.tracks.size == 0) {
            Toast.makeText(requireContext(), R.string.no_tracks_in_playlist, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.sharePlaylist(createPlaylistInfoForSharing())
        }
    }

    private fun getTrackListBottomSheetCollapseHeight(): Int {
        val locationStart = IntArray(2)
        val locationAnchor = IntArray(2)
        val screenHeight = requireContext().resources.displayMetrics.heightPixels
        binding.root.getLocationOnScreen(locationStart)
        binding.Anchor.getLocationInWindow(locationAnchor)
        return screenHeight - locationAnchor[1] + locationStart[1]
    }

    private fun createPlaylistInfoForSharing():String {
        val playlistInfo = mutableListOf(
            "${currentPlaylist.playlistName}\n",
            "${currentPlaylist.playlistDescription}\n",
            "${currentPlaylist.tracksCount} ${getCorrectWord(currentPlaylist.tracksCount)}\n"
        )

        val tracksInfo = trackAdapter.tracks.map { track ->
            "${trackAdapter.tracks.indexOf(track) + 1}. ${track.artistName} - ${track.trackName} " +
                    "(${trackTimeFormater.format(track.trackTimeMillis)})\n"
        }

        playlistInfo.addAll(tracksInfo)

        return playlistInfo.fold("") { info, string ->
            info + string
        }
    }

    private fun getCorrectWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> requireContext().getString(R.string.track_1)
            count % 10 in 2..4 && count % 100 !in 12..14 -> requireContext().getString(R.string.track_24)
            else -> requireContext().getString(R.string.track_other)
        }
    }

    private fun manageTrackListItemClick(track: Track) {
        findNavController().navigate(
            R.id.action_playlistInsideFragment_to_playerActivity,
            PlayerActivity.createArgs(createJsonFromTrack(track))
        )
    }

    private fun clickListItemDebounce(): Boolean {
        val current = isTrackListItemClickAllowed
        if (isTrackListItemClickAllowed) {
            isTrackListItemClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isTrackListItemClickAllowed = true
            }
        }
        return current
    }

    private fun exitPlaylist() {
        findNavController().navigateUp()
    }

    companion object {
        const val MAX_MENU_HEIGHT_DP: Float = 384f
        const val PLAYLIST_KEY = "playlist_key"
        private const val CLICK_DEBOUNCE_DELAY = 1500L
    }
}