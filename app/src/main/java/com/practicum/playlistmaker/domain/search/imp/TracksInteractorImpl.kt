package com.practicum.playlistmaker.domain.search.imp

import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.api.TracksRepository
import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { resource ->
            when (resource) {
                is Resource.Success -> Pair(resource.data, null)
                is Resource.Error -> Pair(null, resource.message)
            }
        }
    }


    override suspend fun getTrackListHistory(): ArrayList<Track>? {
        return repository.getHistory()
    }

    override suspend fun cleanTrackListHistory() {
        repository.cleanHistory()
    }

    override suspend fun putTrackToHistoryList(track: Track) {
        var restoreList = getTrackListHistory()
        if (restoreList != null) {
            val indexOfTwin = restoreList.indexOfFirst { track.trackId == it.trackId }

            if (indexOfTwin != -1) {
                restoreList.removeAt(indexOfTwin)
            }

            restoreList.add(0, track)

            if (restoreList.size > 10) {
                restoreList.removeAt(restoreList.size - 1)
            }

            repository.saveHistory(restoreList)

        } else {
            restoreList = arrayListOf()
            restoreList.add(track)
            repository.saveHistory(restoreList)
        }
    }

}