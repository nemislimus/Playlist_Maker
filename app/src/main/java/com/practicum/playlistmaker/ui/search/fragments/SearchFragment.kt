package com.practicum.playlistmaker.ui.search.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.ui.createJsonFromTrack
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.TrackAdapter
import com.practicum.playlistmaker.ui.search.models.TracksState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // Placeholder elements
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var historyText: TextView

    // Others Views
    private lateinit var outOfSearchButton: Toolbar
    private lateinit var searchBar: EditText
    private lateinit var searchBarClearButton: ImageView
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var searchProgressBar: ProgressBar

    private var trackAdapter = TrackAdapter(
        { if (clickListItemDebounce()) manageListItemClick(it) },
        { clearHistory() },
    )

    // Util variables
    private var searchBarTextValue: String = EMPTY_TEXT_VALUE
    private var isListItemClickAllowed: Boolean = true
    private val searchActivityHandler = Handler(Looper.getMainLooper())
    private var searchBarTextWatcher: TextWatcher? = null
    private val viewModel: TracksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outOfSearchButton = binding.searchOutButton
        searchBar = binding.searchBarEditText
        searchBarClearButton = binding.searchBarClearButton
        trackListRecyclerView = binding.rvTrackList
        searchProgressBar = binding.searchProgressBar

        placeholderImage = binding.llPlaceholderLayout.ivPlaceholderImage
        placeholderText = binding.llPlaceholderLayout.tvPlaceholderText
        placeholderRefreshButton = binding.llPlaceholderLayout.btnPlaceholderRefresh
        placeholderLayout = binding.llPlaceholderLayout.root

        historyText = binding.tvSearchHistoryText

        placeholderRefreshButton.setOnClickListener {
            viewModel.searchOnRefresh(searchBarTextValue)
        }

        binding.searchOutButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        searchBarClearButton.setOnClickListener {
            searchBarTextValue = EMPTY_TEXT_VALUE
            searchBar.setText(EMPTY_TEXT_VALUE)
            hideSearchBarKeyboard()
            viewModel.showHistory()
        }

        // Handle history display when the focus is on searchBar
        searchBar.setOnFocusChangeListener { _, hasFocus ->
            manageHistoryVisibilityOnChanges(hasFocus, searchBar.text.toString())
        }

        // TextWatcher
        searchBarTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                manageContentVisibilityOnChanges(s.toString())
                manageHistoryVisibilityOnChanges(searchBar.hasFocus(), s.toString())
                searchBarClearButton.isVisible = clearSearchBarButtonVisibility(s)
                searchBarTextValue = s.toString()
                viewModel.searchDebounce(s.toString())
            }
        }

        searchBar.addTextChangedListener(searchBarTextWatcher)

        // Configure RecyclerView
        trackListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackAdapter

    }

    override fun onResume() {
        super.onResume()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        searchBarTextWatcher?.let { searchBar.removeTextChangedListener(it) }
        _binding = null
        super.onDestroyView()
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.History -> showHistory(state.historyTracks)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.Empty -> showEmpty(state.emptyMessage)
        }
    }

    private fun showLoading() {
        trackListRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        searchProgressBar.isVisible = true
    }

    private fun showError(errorMessage: String, showButton: Boolean = true) {
        trackListRecyclerView.isVisible = false
        searchProgressBar.isVisible = false
        placeholderLayout.isVisible = true
        placeholderImage.isVisible = true
        placeholderText.isVisible = true
        placeholderText.text = errorMessage
        placeholderRefreshButton.isVisible = showButton

        placeholderImage.setImageResource(
            if (showButton) R.drawable.no_internet else R.drawable.not_found
        )
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage, showButton = false)
    }

    private fun showContent(tracks: List<Track>) {
        trackListRecyclerView.isVisible = true
        searchProgressBar.isVisible = false
        placeholderLayout.isVisible = false

        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistory(historyTracks: List<Track>) {
        historyText.isVisible = historyTracks.isNotEmpty()
        showContent(historyTracks)
    }

    private fun clearSearchBarButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun hideSearchBarKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchBar.windowToken, 0)
    }

    private fun manageHistoryVisibilityOnChanges(focus: Boolean, text: String?) {

        if (viewModel.observeState().value !is TracksState.History) return
        if (text == searchBarTextValue) return

        if (focus && text?.isEmpty() == true) {
            viewModel.showHistory()
        } else {
            viewModel.hideHistory()
        }
    }

    private fun manageContentVisibilityOnChanges(text: String?) {
        if ( (viewModel.observeState().value is TracksState.Content) && text?.isEmpty() == true )
            viewModel.showHistory()
    }

    private fun manageListItemClick(track: Track) {

        viewModel.putTrackToHistory(track)

        if (historyText.isVisible) {
            trackAdapter.notifyDataSetChanged()
            trackListRecyclerView.scrollToPosition(0)
        }

        // Go to Player by clicked Track
        val playerIntent = Intent(requireActivity(), PlayerActivity::class.java).apply {
            putExtra(PlaylistApp.TRACK_KEY_FROM_SEARCH_TO_PLAYER, createJsonFromTrack(track))
        }
        startActivity(playerIntent)
    }

    private fun clearHistory() {
        viewModel.eraseHistory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EDIT_TEXT_VALUE, searchBarTextValue)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        searchBarTextValue = savedInstanceState?.getString(EDIT_TEXT_VALUE) ?: EMPTY_TEXT_VALUE
        searchBar.setText(searchBarTextValue)
    }

    private fun clickListItemDebounce(): Boolean {
        val current = isListItemClickAllowed
        if (isListItemClickAllowed) {
            isListItemClickAllowed = false
            searchActivityHandler.postDelayed(
                { isListItemClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    companion object {
        const val TAG = "search_tag"

        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        private const val EMPTY_TEXT_VALUE= ""
    }
}