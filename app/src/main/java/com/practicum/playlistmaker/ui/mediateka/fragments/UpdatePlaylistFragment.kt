package com.practicum.playlistmaker.ui.mediateka.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.mediateka.view_models.NewPlaylistFragmentViewModel
import com.practicum.playlistmaker.ui.mediateka.view_models.UpdatePlaylistFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UpdatePlaylistFragment: NewPlaylistFragment() {

    private lateinit var currentPlaylist: Playlist
    private var editPlaylistTitle = ""
    private var editPlaylistDescription = ""
    private var coverAndNameWasChanged = false

    override val viewModel: NewPlaylistFragmentViewModel
        get() {
            val updateViewModel by viewModel<UpdatePlaylistFragmentViewModel>() {
                parametersOf(requireArguments().getLong(CURRENT_PLAYLIST_ID_KEY))
            }
            return updateViewModel
        }

    private lateinit var updateViewModel: UpdatePlaylistFragmentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateViewModel = viewModel as UpdatePlaylistFragmentViewModel

        updateViewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            currentPlaylist = playlist
        }

        setCurrentPlaylistValues()
    }

    override fun createPlaylist() {
        if (playlistWasChanged()) {
            viewLifecycleOwner.lifecycleScope.launch {

                // actions on playlist cover changed
                if(pickedPhotoUri != null) {
                    if(binding.EditTitle.text.toString() != currentPlaylist.playlistName) {
                        coverAndNameWasChanged = true
                        saveCover(pickedPhotoUri!!, binding.EditTitle.text.toString(), false)
                        deleteOldCover()
                    } else {
                        saveCover(pickedPhotoUri!!, binding.EditTitle.text.toString(), false)
                    }
                }

                // actions on playlist name changed
                if (binding.EditTitle.text.toString() != currentPlaylist.playlistName) {
                    if(coverAndNameWasChanged) {
                        editPlaylistTitle = binding.EditTitle.text.toString()
                    } else {
                        saveCover(
                            Uri.parse(currentPlaylist.coverPath),
                            binding.EditTitle.text.toString(),
                            true
                        )
                        editPlaylistTitle = binding.EditTitle.text.toString()
                        deleteOldCover()
                    }
                }

                if (binding.EditDescription.text.toString() != currentPlaylist.playlistDescription)
                    editPlaylistDescription = binding.EditDescription.text.toString()

                val playlist = createPlaylistForSavingToDatabase()

                val updatePlaylistJob = launch(Dispatchers.IO) {
                    updateViewModel.updatePlaylist(playlist)
                }
                updatePlaylistJob.join()

                Toast.makeText(requireContext(), createToastMessage(), Toast.LENGTH_SHORT).show()
                onExitClick()
            }

        } else {
            onExitClick()
        }
    }

    override fun createPlaylistForSavingToDatabase(): Playlist =
        Playlist(
            id = currentPlaylist.id,
            playlistName = editPlaylistTitle,
            playlistDescription = editPlaylistDescription,
            coverPath = coverFilePathString,
            trackIdList = currentPlaylist.trackIdList,
            tracksCount = currentPlaylist.tracksCount
        )

    override fun createToastMessage(): String {
        val plName = binding.EditTitle.text.toString()
        val pl = requireContext().getString(R.string.playlist_pl)
        val ed = requireContext().getString(R.string.playlist_edit)
        return "$pl $plName $ed"
    }

    override fun onExitClick() {
        findNavController().navigateUp()
    }

    private fun setCurrentPlaylistValues() {
        viewLifecycleOwner.lifecycleScope.launch {
            val getPlaylistJob = launch(Dispatchers.IO) {
                updateViewModel.getCurrentPlaylistFromDb()
            }
            getPlaylistJob.join()
            renderCurrentPlaylistValues()
        }
    }

    private fun renderCurrentPlaylistValues() {
        coverFilePathString = currentPlaylist.coverPath
        editPlaylistTitle = currentPlaylist.playlistName
        editPlaylistDescription = currentPlaylist.playlistDescription
        with(binding) {
            tbBackFromNewPlaylist.title = requireContext().getString(R.string.edit_playlist_title)
            CreatePlaylistButton.text = requireContext().getString(R.string.edit_playlist_edit_button)
            ivAddPlaylistPhoto.setImageURI(Uri.parse(coverFilePathString))
            EditTitle.setText(currentPlaylist.playlistName)
            EditDescription.setText(currentPlaylist.playlistDescription)
        }
    }

    private suspend fun deleteOldCover() {
        val deleteCoverJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            updateViewModel.deleteCoverFromPrivateStorage(currentPlaylist.coverPath)
        }
        deleteCoverJob.join()
    }

    private fun playlistWasChanged(): Boolean {
        return pickedPhotoUri != null ||
                binding.EditTitle.text.toString() != currentPlaylist.playlistName ||
                binding.EditDescription.text.toString() != currentPlaylist.playlistDescription
    }

    companion object {
        const val CURRENT_PLAYLIST_ID_KEY = "count_key"

        fun createArgs(playlistId: Long): Bundle =
           bundleOf(CURRENT_PLAYLIST_ID_KEY to playlistId)
    }
}