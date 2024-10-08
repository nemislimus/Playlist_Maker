package com.practicum.playlistmaker.ui.player.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.dpToPx
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.ui.convertTimeValueFromLongToString
import com.practicum.playlistmaker.ui.player.model.PlayerUiState
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteTracksInteractor,
    private val currentTrack: Track,
    private val context: Context,
) : ViewModel() {

    private var playerIsPrepared: Boolean = false
    lateinit var onPrepare: () -> Unit

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

        val updateDuration: String = convertTimeValueFromLongToString(track.trackTimeMillis)

        return PlayerUiState(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackDuration = updateDuration,
            coverLink = track.artworkUrl512,
            coverCornerRadius = RoundedCorners(dpToPx(8f, context)),
            soundPreview = track.previewUrl,
            hasCollection = track.collectionName.isNotEmpty(),
            collectionName = track.collectionName,
            releaseDate = track.releaseDate.substring(0, 4),
            genreName = track.primaryGenreName,
            country = track.country,
            isFavorite = track.isFavorite,
       )
    }

    fun setPlayButtonAsPrepared (onChange: () -> Unit) {
        onPrepare = onChange
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (currentTrack.isFavorite) {
                favoriteInteractor.removeFromFavorite(currentTrack)
                changeFavoriteState()
            } else {
                favoriteInteractor.addToFavorite(currentTrack)
                changeFavoriteState()
            }
        }
    }

    private fun changeFavoriteState() {
        currentTrack.isFavorite = !currentTrack.isFavorite
        playerUiStateLiveData.postValue(getUiState())
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

}