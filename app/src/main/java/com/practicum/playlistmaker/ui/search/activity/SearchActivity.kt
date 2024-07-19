package com.practicum.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.ui.createJsonFromTrack
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.PlaylistApp
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.TrackAdapter
import com.practicum.playlistmaker.ui.search.models.TracksState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

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
    private var searchBarTextValue: String = ""
    private var isListItemClickAllowed: Boolean = true
    private val searchActivityHandler = Handler(Looper.getMainLooper())
    private var searchBarTextWatcher: TextWatcher? = null
    private lateinit var viewModel: TracksViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TracksViewModel.getViewModelFactory()
        )[TracksViewModel::class.java]

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
            finish()
        }

        searchBarClearButton.setOnClickListener {
            searchBarTextValue = ""
            searchBar.setText("")
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
                manageHistoryVisibilityOnChanges(searchBar.hasFocus(), s.toString())
                searchBarClearButton.visibility = clearSearchBarButtonVisibility(s)
                searchBarTextValue = s.toString()
                viewModel.searchDebounce(s.toString())
            }
        }

        searchBar.addTextChangedListener(searchBarTextWatcher)

        // Configure RecyclerView
        trackListRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackAdapter
    }

    override fun onResume() {
        super.onResume()

        viewModel.observeState().observe(this) {
            render(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchBarTextWatcher?.let { searchBar.removeTextChangedListener(it) }
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

    private fun clearSearchBarButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideSearchBarKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

    private fun manageListItemClick(track: Track) {

        viewModel.putTrackToHistory(track)

        if (historyText.visibility == View.VISIBLE) {
            trackAdapter.notifyDataSetChanged()
            trackListRecyclerView.scrollToPosition(0)
        }

        // Go to Player by clicked Track
        val playerIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlaylistApp.TRACK_KEY, createJsonFromTrack(track))
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchBarTextValue = savedInstanceState.getString(EDIT_TEXT_VALUE).toString()
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
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
    }
}