package com.practicum.playlistmaker.ui.search.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.search.models.TracksState
import com.practicum.playlistmaker.util.jobWithDebounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TracksViewModel(
    private val tracksInteractor: TracksInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val context: Context
) : ViewModel() {

    private var historyTrackList: ArrayList<Track> = arrayListOf()
    private var latestSearchText: String? = null

    private val tracksSearchDebounce = jobWithDebounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { newText -> searchRequest(newText) }

    private var tracksStateLiveData =
        MutableLiveData<TracksState>()

    fun observeState(): LiveData<TracksState> = tracksStateLiveData

    init {
        showHistory()
    }

    suspend fun updateFavoriteStateOnResume(tracks: List<Track>): List<Track> {
        val favoritesId = favoriteTracksInteractor.getAllFavoriteId().toSet()
        return tracks.map { track ->
            track.copy(isFavorite = favoritesId.contains(track.trackId))
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            updateState(TracksState.Loading)

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
                updateState(
                    TracksState.Error(
                        errorMessage = context.getString(R.string.search_error_no_internet),
                    )
                )
            }

            tracks.isEmpty() -> {
                updateState(
                    TracksState.Empty(
                        emptyMessage = context.getString(R.string.search_error_not_found),
                    )
                )
            }

            else -> {
                updateState(
                    TracksState.Content(
                        tracks = tracks,
                    )
                )
            }
        }
    }

    private fun updateState(state: TracksState) {
        tracksStateLiveData.postValue(state)
    }

    private suspend fun getHistory(): ArrayList<Track> {
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

    fun showHistory() {
        viewModelScope.launch {
            val history = async(Dispatchers.IO) {
                getHistory()
            }
            historyTrackList = history.await()
            updateState(TracksState.History(historyTrackList))
        }
    }

    fun hideHistory(state: TracksState = TracksState.History(emptyHistory)) {
        updateState(state)
    }

    fun putTrackToHistory(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if (tracksStateLiveData.value is TracksState.History) {
                val trackToHistory = launch {
                    tracksInteractor.putTrackToHistoryList(track)
                }
                trackToHistory.join()
                showHistory()
            } else if (tracksStateLiveData.value is TracksState.Content) {
                tracksInteractor.putTrackToHistoryList(track)
            }
        }
    }

    fun eraseHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val erase = launch {
                tracksInteractor.cleanTrackListHistory()
            }
            erase.join()
            showHistory()
        }
    }

    fun searchOnRefresh(repeatText: String) {
        searchRequest(repeatText)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

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