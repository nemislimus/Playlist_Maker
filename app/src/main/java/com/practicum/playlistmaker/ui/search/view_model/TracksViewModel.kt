package com.practicum.playlistmaker.ui.search.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.search.models.TracksState
import com.practicum.playlistmaker.util.jobWithDebounce
import kotlinx.coroutines.launch

class TracksViewModel(
    private val tracksInteractor: TracksInteractor,
    private val context: Context
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    private val tracksSearchDebounce = jobWithDebounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { newText -> searchRequest(newText) }

    private var tracksStateLiveData =
        MutableLiveData<TracksState>(TracksState.History(getHistory()))

    fun observeState(): LiveData<TracksState> = tracksStateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText)
                    .collect { resultData ->
                        consumeSearchResult(resultData.first, resultData.second)
                    }
            }
        }
    }

    private fun consumeSearchResult(foundTracks: List<Track>?, message: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            message != null -> {
                renderState(
                    TracksState.Error(
                        errorMessage = context.getString(R.string.search_error_no_internet),
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    TracksState.Empty(
                        emptyMessage = context.getString(R.string.search_error_not_found),
                    )
                )
            }

            else -> {
                renderState(
                    TracksState.Content(
                        tracks = tracks,
                    )
                )
            }
        }
    }

    private fun renderState(state: TracksState) {
        tracksStateLiveData.postValue(state)
    }

    private fun getHistory(): ArrayList<Track> {
        return updateHistory(tracksInteractor.getTrackListHistory())
    }

    private fun updateHistory(historyList: ArrayList<Track>?): ArrayList<Track> {
        var updateList: ArrayList<Track> = arrayListOf()
        if (!historyList.isNullOrEmpty()) {
            updateList = historyList
            updateList.add(trackForButtonInflate)
        }
        return updateList
    }

    fun showHistory(state: TracksState = TracksState.History(getHistory())) {
        renderState(state)
    }

    fun hideHistory(state: TracksState = TracksState.History(emptyHistory)) {
        renderState(state)
    }

    fun putTrackToHistory(track: Track) {
        if (tracksStateLiveData.value is TracksState.History) {
            tracksInteractor.putTrackToHistoryList(track)
            showHistory()
        } else if (tracksStateLiveData.value is TracksState.Content) {
            tracksInteractor.putTrackToHistoryList(track)
        }
    }

    fun eraseHistory() {
        tracksInteractor.cleanTrackListHistory()
        showHistory()
    }

    fun searchOnRefresh(repeatText: String) {
        searchRequest(repeatText)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        super.onCleared()

    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        private val emptyHistory: ArrayList<Track> = arrayListOf()

        private val trackForButtonInflate: Track = Track(
            -1,
            "plm",
            "plm",
            210743,
            "plm",
            "plm",
            "plm",
            "plm",
            "plm",
            "plm",
        )
    }
}