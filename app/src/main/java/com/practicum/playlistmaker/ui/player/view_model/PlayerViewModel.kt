package com.practicum.playlistmaker.ui.player.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.domain.db.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.domain.db.api.PlaylistsInteractor
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.dpToPx
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.ui.convertTimeValueFromLongToString
import com.practicum.playlistmaker.ui.mediateka.models.PlaylistsState
import com.practicum.playlistmaker.ui.player.model.PlayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteTracksInteractor,
    private val currentTrack: Track,
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var playerIsPrepared: Boolean = false
    lateinit var onPrepare: () -> Unit
    var playerNotStarted = true

    private var playerStateLiveData =
        MutableLiveData<PlayerState>()

    private var playerUiStateLiveData =
        MutableLiveData(getUiState())

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getPlayerUiStateLiveData(): LiveData<PlayerUiState> = playerUiStateLiveData

    private val _playlists = MutableStateFlow<PlaylistsState>(PlaylistsState.Loading)
    fun playlists() = _playlists.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun getPlaylists() {
        viewModelScope.launch { //this: CoroutineScope
            playlistsInteractor.getAllPlaylists().collect {
                    playlistFromDb -> consumeDbData(playlistFromDb)
            }
        }
    }

    suspend fun addTrackIdToPlaylistById(playlistId: Long, trackId: Long): Int =
        playlistsInteractor.addTrackIdToPlaylistById(playlistId, trackId)

    suspend fun saveTrackToPlaylistPool(track: Track) =
        playlistsInteractor.saveTrackToPlaylistPool(track)


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

    fun getCurrentTimerPosition(): String {
        return playerInteractor.getPlayerCurrentTimerPosition()
    }

    fun getPlayerState(): PlayerState {
        return playerInteractor.getPlayerState()
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

    fun setPlayButtonAsPrepared (onChange: () -> Unit) {
        onPrepare = onChange
    }

    private fun consumeDbData(data: List<Playlist>) {
        if (data.isNotEmpty()) {
            _playlists.update { PlaylistsState.Content(data) }
        } else {
            _playlists.update { PlaylistsState.Empty }
        }
    }

    private fun setPlayerState(playerState: PlayerState = getPlayerState()) {
        playerStateLiveData.postValue(playerState)
    }

    private fun releasePlayer() {
        playerInteractor.release()
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

    private fun changeFavoriteState() {
        currentTrack.isFavorite = !currentTrack.isFavorite
        playerUiStateLiveData.postValue(getUiState())
    }
}