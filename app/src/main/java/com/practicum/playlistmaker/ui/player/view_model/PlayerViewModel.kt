package com.practicum.playlistmaker.ui.player.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.dpToPx
import com.practicum.playlistmaker.ui.player.model.PlayerState
import com.practicum.playlistmaker.ui.player.model.PlayerUiState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    val currentTrack: Track,
) : AndroidViewModel(application) {

    private var playerIsPrepared: Boolean = false
    lateinit var onPrepare: () -> Unit
    private val playerInteractor = Creator.provideMediaPlayerInteractor()

    private var playerStateLiveData =
        MutableLiveData<PlayerState>()

    private var playerUiStateLiveData =
        MutableLiveData(getUiState())

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getPlayerUiStateLiveData(): LiveData<PlayerUiState> = playerUiStateLiveData

    private fun setPlayerState(playerState: PlayerState = getPlayerState()) {
        playerStateLiveData.postValue(playerState)
    }

    fun preparePlayer() {
        if (!playerIsPrepared) {
            playerInteractor.setPlayerDataSource(currentTrack)
            playerInteractor.prepare(
                listener = object : PlayerInteractor.OnStateChangeListener {
                    override fun onChange() {
                        setPlayerState()
                        onPrepare.invoke()
                    }
                }
            )
            playerIsPrepared = !playerIsPrepared
        } else {
            onPrepare.invoke()
        }
    }

    fun playTrack() {
        playerInteractor.start()
    }

    fun pauseTrack() {
        playerInteractor.pause()
    }

    private fun releasePlayer() {
        playerInteractor.release()
    }

    fun getCurrentTimerPosition(): String {
        return playerInteractor.getPlayerCurrentTimerPosition()
    }

    fun getPlayerState(): PlayerState {
        return playerInteractor.getPlayerState()
    }

    private fun getUiState(track: Track = currentTrack): PlayerUiState {

        val updateDuration: String =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        return PlayerUiState(
           trackId = track.trackId,
           trackName = track.trackName,
           artistName = track.artistName,
           trackDuration = updateDuration,
           coverLink = track.artworkUrl512,
           coverCornerRadius = RoundedCorners(dpToPx(8f, getApplication())),
           soundPreview = track.previewUrl,
           hasCollection = track.collectionName.isNotEmpty(),
           collectionName = track.collectionName,
           releaseDate = track.releaseDate.substring(0, 4),
           genreName = track.primaryGenreName,
           country = track.country,
       )
    }

    fun setPlayButtonAsPrepared (onChange: () -> Unit) {
        onPrepare = onChange
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    currentTrack = track,
                )
            }
        }

    }
}