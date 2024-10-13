package com.practicum.playlistmaker.ui.mediateka.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.ui.MainActivity
import com.practicum.playlistmaker.ui.mediateka.view_models.NewPlaylistFragmentViewModel
import com.practicum.playlistmaker.ui.player.activity.FragmentContainerDisabler
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var exitDialog: MaterialAlertDialogBuilder
    private lateinit var backPressedCallback: OnBackPressedCallback

    private var titleTextWatcher: TextWatcher? = null
    private var coverFilePathString: String = ""

    private var existingPlaylistCount: Int = 0

    private val newPlaylistFragmentViewModel by viewModel<NewPlaylistFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        existingPlaylistCount = requireArguments().getInt(PLAYLIST_COUNT_KEY)

        // Lock screen rotation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set exit dialog
        exitDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(requireContext().getString(R.string.dialog_title))
            .setMessage(requireContext().getString(R.string.dialog_message))
            .setNeutralButton(requireContext().getString(R.string.dialog_neutral)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(requireContext().getString(R.string.dialog_done)) { _, _ ->
                exitOnCompletion()
            }

        backPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExitClick()
            }
        }

        // Pick photo from device and save cover in external storage
        val pickPlaylistCover = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)  {
                binding.ivAddPlaylistPhoto.setImageURI(uri)
                viewLifecycleOwner.lifecycleScope.launch {
                    val filePath = async(Dispatchers.IO) {
                        newPlaylistFragmentViewModel.saveCoverToPrivateStorage(
                            requireContext(),
                            uri,
                            existingPlaylistCount
                        )
                    }
                    coverFilePathString = filePath.await()
                }
            }
        }

        titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                binding.CreatePlaylistButton.isEnabled = !s.isNullOrBlank()
            }
        }

        binding.EditTitle.addTextChangedListener(titleTextWatcher)

        // manage title field soft input done
        binding.EditTitle.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.EditDescription.text.isNullOrBlank()) {
                    binding.EditDescription.requestFocus()
                } else hideSoftKeyboard(v)
                true
            }
            false
        }

        // manage description field soft input done
        binding.EditDescription.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.EditTitle.text.isNullOrBlank()) {
                    binding.EditTitle.requestFocus()
                } else hideSoftKeyboard(v)
                true
            }
            false
        }

        // GET COVER FOR PLAYLIST
        binding.ivAddPlaylistPhoto.setOnClickListener {
            pickPlaylistCover.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // CREATE PLAYLIST
        binding.CreatePlaylistButton.setOnClickListener {
            val playlist = createPlaylistForSavingToDatabase()

            viewLifecycleOwner.lifecycleScope.launch {
                val saveJob = launch(Dispatchers.IO) {
                    newPlaylistFragmentViewModel.savePlaylist(playlist)
                }
                saveJob.join()

                Toast.makeText(requireContext(), createToastMessage(), Toast.LENGTH_SHORT).show()
                exitOnCompletion()
            }
        }

        binding.tbBackFromNewPlaylist.setOnClickListener {
            onExitClick()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun onDestroyView() {
        _binding = null
        titleTextWatcher = null

        // Unlock screen rotation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        super.onDestroyView()
    }

    private fun createPlaylistForSavingToDatabase(): Playlist =
        Playlist(
            playlistName = binding.EditTitle.text.toString(),
            playlistDescription = binding.EditDescription.text.toString(),
            coverPath = coverFilePathString,
            trackIdList = emptyList<Long>(),
            0
        )

    private fun createToastMessage(): String {
        val plName = binding.EditTitle.text.toString()
        val pl = requireContext().getString(R.string.playlist_pl)
        val cr = requireContext().getString(R.string.playlist_create)
        return "$pl $plName $cr"
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onExitClick() {
        when(requireActivity()) {

            is MainActivity -> {
                if (checkContentFilling()) {
                    exitDialog.show()
                } else {
                    findNavController().navigateUp()
                }
            }

            is PlayerActivity -> {
                if (checkContentFilling()) {
                    exitDialog.show()
                } else {
                    parentFragmentManager.popBackStack()
                    (requireActivity() as? FragmentContainerDisabler)?.disableFragmentContainer()
                }
            }
        }
    }

    private fun checkContentFilling(): Boolean {
        return !binding.EditTitle.text.isNullOrBlank() ||
                !binding.EditDescription.text.isNullOrBlank() ||
                binding.ivAddPlaylistPhoto.drawable != null
    }

    private fun exitOnCompletion() {
        when(requireActivity()) {
            is PlayerActivity -> {
                parentFragmentManager.popBackStack()
                (requireActivity() as? FragmentContainerDisabler)?.disableFragmentContainer()
            }
            is MainActivity -> findNavController().navigateUp()
        }
    }

    companion object {
        const val TAG = "NPLF"
        const val PLAYLIST_COUNT_KEY = "count_key"

        fun newInstance(playlistCount: Int) = NewPlaylistFragment().apply {
            arguments = bundleOf(PLAYLIST_COUNT_KEY to playlistCount)
        }

        fun createArgs(playlistCount: Int): Bundle =
            bundleOf(PLAYLIST_COUNT_KEY to playlistCount)
    }
}