package com.practicum.playlistmaker.domain.search.imp

import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.api.TracksRepository
import com.practicum.playlistmaker.domain.search.models.Resource
import com.practicum.playlistmaker.domain.search.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> {consumer.consume(null, resource.message )}
            }
        }
    }

    override fun getTrackListHistory(): ArrayList<Track>? {
        return repository.getHistory()
    }

    override fun cleanTrackListHistory() {
        repository.cleanHistory()
    }

    override fun putTrackToHistoryList(track: Track) {
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