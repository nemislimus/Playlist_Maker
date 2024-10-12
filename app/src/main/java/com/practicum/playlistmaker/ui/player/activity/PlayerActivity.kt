package com.practicum.playlistmaker.ui.player.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.createTrackFromJson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.db.models.Playlist
import com.practicum.playlistmaker.domain.player.models.PlayerState
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.ui.mediateka.PlaylistsAdapter
import com.practicum.playlistmaker.ui.mediateka.fragments.NewPlaylistFragment
import com.practicum.playlistmaker.ui.mediateka.models.PlaylistsState
import com.practicum.playlistmaker.ui.player.model.PlayerUiState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity(), FragmentContainerDisabler {
    private lateinit var binding: ActivityPlayerBinding

    private var timerJob: Job? = null
    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var currentTrack: Track

    private val playlistsAdapter =
        PlaylistsAdapter(isPlayerPlaylist = true){ playlist ->
            lifecycleScope.launch {
                val clickJob = launch {
                    manageClickOnTrackAdding(playlist.playlistName, currentTrack.trackId)
                }
                clickJob.join()
                delay(50)
                viewModel.getPlaylists()
            }
        }

    private val viewModel: PlayerViewModel by viewModel{
        parametersOf( intent.getStringExtra(ARGS_TRACK)?.let { createTrackFromJson(it) } )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = intent.getStringExtra(ARGS_TRACK)?.let { createTrackFromJson(it) }!!

        bottomSheetBehavior = BottomSheetBehavior.from(binding.BottomSheetLayout.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = darkFadeControl(slideOffset)
            }
        })

        // Configure RecyclerView
        binding.BottomSheetLayout.rvPlaylistsList.adapter = playlistsAdapter

        binding.ivPlayButton.setOnClickListener {
            when(viewModel.getPlayerState()) {
                PlayerState.PREPARED, PlayerState.PAUSED -> viewModel.playTrack()
                PlayerState.PLAYING -> viewModel.pauseTrack()
                PlayerState.DEFAULT -> Unit
            }
        }

        binding.ivAddToPlaylistButton.setOnClickListener {
//            trackInPlaylist = !trackInPlaylist
//            setButtonState(it, trackInPlaylist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.ivAddToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.tbBackFromPlayerButton.setOnClickListener {
            finish()
        }

        binding.BottomSheetLayout.createNewPlaylistButton.setOnClickListener {
            enableFragmentContainer()
            supportFragmentManager.commit {
                add(
                    R.id.PlayerFragmentContainer,
                    NewPlaylistFragment.newInstance(playlistsAdapter.playlists.size)
                )
                addToBackStack(NewPlaylistFragment.TAG)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                setReorderingAllowed(true)
            }
        }

        viewModel.apply {
            getPlayerUiStateLiveData().observe(this@PlayerActivity) { stateUi ->
                setUi(stateUi)
            }

            getPlayerStateLiveData().observe(this@PlayerActivity) { playerState ->
                managePlayer(playerState)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        with(viewModel) {
            setPlayButtonAsPrepared { updatePlayButtonAlpha() }
            preparePlayer()
        }
        //Observe playlists StateFlow
        showPlaylists()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseTrack()
    }

    private fun showPlaylists() {
        lifecycleScope.launch {
            viewModel.playlists().collect { playlistsState ->
                renderState(playlistsState)
            }
        }
    }

    private fun renderState(playlistsState: PlaylistsState) {
        when(playlistsState) {
            is PlaylistsState.Loading -> showNoting()
            is PlaylistsState.Empty -> showNoting()
            is PlaylistsState.Content -> showContent(playlistsState.playlists)
        }
    }

    private fun showNoting() {
        binding.BottomSheetLayout.rvPlaylistsList.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.BottomSheetLayout.rvPlaylistsList.isVisible = true
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IN_PLAYLIST_VALUE, trackInPlaylist)
        outState.putBoolean(IN_FAVORITE_VALUE, trackInFavorites)
        outState.putString(VALUE_TIMER, binding.tvTrackTimer.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackInPlaylist = savedInstanceState.getBoolean(IN_PLAYLIST_VALUE)
        trackInFavorites = savedInstanceState.getBoolean(IN_FAVORITE_VALUE)
        val timerValue = savedInstanceState.getString(VALUE_TIMER).toString()

        binding.tvTrackTimer.text = timerValue
        setButtonState(binding.ivAddToPlaylistButton, trackInPlaylist)
        setButtonState(binding.ivAddToFavoritesButton, trackInFavorites)
    }

    private fun setUi(stateUi: PlayerUiState) {

        Glide.with(this)
            .load(stateUi.coverLink)
            .placeholder(R.drawable.ic_placeholder_track_image)
            .transform(
                CenterCrop(), stateUi.coverCornerRadius
            )
            .into(binding.ivAlbumCover)

        with(binding) {
            albumInfoGroup.isVisible = stateUi.hasCollection
            if (stateUi.hasCollection) tvTrackAlbumValue.text = stateUi.collectionName
            tvTrackNamePlayer.text = stateUi.trackName
            tvArtistNamePlayer.text = stateUi.artistName
            tvTrackDurationValue.text = stateUi.trackDuration
            tvTrackTimer.text = ZERO_TIMER
            tvTrackReleaseDateValue.text = stateUi.releaseDate
            tvTrackGenreValue.text = stateUi.genreName
            tvTrackCountryValue.text = stateUi.country

            ivAddToFavoritesButton.setImageResource(
                if (stateUi.isFavorite) R.drawable.added_in_favorites else R.drawable.add_to_favorite
            )
        }
    }

    private fun updatePlayButtonAlpha() {
        binding.ivPlayButton.alpha = 1.0f
    }

    private fun managePlayer(playerState: PlayerState) {
        when(playerState) {
             PlayerState.PREPARED -> {
                binding.ivPlayButton.isEnabled = true
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                 manageTimerByPlayerState(playerState)
            }

            PlayerState.PLAYING -> {
                binding.ivPlayButton.setImageResource(R.drawable.pause_button)
                manageTimerByPlayerState(playerState)
            }

            PlayerState.PAUSED -> {
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                manageTimerByPlayerState(playerState)
            }

            PlayerState.DEFAULT -> {
                binding.ivPlayButton.isEnabled = false
                binding.ivPlayButton.setImageResource(R.drawable.play_button)
                manageTimerByPlayerState(playerState)
            }
        }
    }

    private fun manageTimerByPlayerState(state: PlayerState) {
        when (state) {
            PlayerState.DEFAULT, PlayerState.PREPARED -> {
                timerJob?.cancel()
                binding.tvTrackTimer.text = ZERO_TIMER
            }

            PlayerState.PLAYING -> startTimer()

            PlayerState.PAUSED -> timerJob?.cancel()
        }
    }

    private fun startTimer() {
        timerJob = lifecycleScope.launch {
            while (viewModel.getPlayerState() == PlayerState.PLAYING) {
                delay(TIMER_DELAY)
                binding.tvTrackTimer.text = viewModel.getCurrentTimerPosition()
            }
        }
    }

    private fun setButtonState(actionView: View, state: Boolean) {
        when {
            (actionView == binding.ivAddToPlaylistButton) -> (actionView as? ImageView)
                ?.setImageResource(if (state) R.drawable.added_in_playlist else R.drawable.add_to_playlist)
        }
    }

    private suspend fun manageClickOnTrackAdding(playlistName: String, trackId: Long?) {
        if (trackId != null){
            lifecycleScope.launch {
                val addTrack = async(Dispatchers.IO) {
                    addTrackToPlaylist(playlistName, trackId)
                }
                val result = addTrack.await()

                withContext(Dispatchers.Main) {
                    when(result) {
                        ADD_TRACK_SUCCESS -> makeToastForTrackAdding(result, playlistName)
                        ADD_TRACK_COLLISION -> makeToastForTrackAdding(result, playlistName)
                    }
                }
            }
        }
    }

    private fun makeToastForTrackAdding(result: Int, playlistName: String) {
        Toast.makeText(this, createToastMessage(result, playlistName), Toast.LENGTH_SHORT)
            .show()
    }

    private fun createToastMessage(result: Int, playlistName: String): String {
        return when(result) {
            ADD_TRACK_SUCCESS -> getString(R.string.track_add_to_playlist) + playlistName
            ADD_TRACK_COLLISION -> getString(R.string.track_add_collision) + playlistName
            else -> getString(R.string.track_add_unreal)
        }
    }

    private suspend fun addTrackToPlaylist(playlistName: String, trackId: Long): Int {
        return viewModel.addTrackToPlaylistByName(playlistName, trackId)
    }

    private fun darkFadeControl(slideOffset: Float): Float {
        val alphaDarkBoost: Float
        val alphaDelta: Float

        if (slideOffset >= 0) {
            alphaDarkBoost = 0.6f
            alphaDelta = 0.4f * slideOffset
        } else {
            alphaDarkBoost = 0.6f
            alphaDelta = 0.6f * slideOffset
        }
        return alphaDarkBoost + alphaDelta
    }

    companion object {
        const val ADD_TRACK_SUCCESS = 2
        const val ADD_TRACK_COLLISION = 1

        private const val ZERO_TIMER = "00:00"
        private const val VALUE_TIMER = "timer_value"
        private const val TIMER_DELAY = 300L

        private const val IN_PLAYLIST_VALUE = "IN_PLAYLIST_VALUE"
        private const val IN_FAVORITE_VALUE = "IN_FAVORITE_VALUE"

        private const val ARGS_TRACK = "args_movie_id"

        fun createArgs(trackJsonString: String): Bundle =
            bundleOf(ARGS_TRACK to trackJsonString)
    }

    override fun disableFragmentContainer() {
        binding.PlayerFragmentContainer.isVisible = false
        binding.BottomSheetLayout.root.isVisible = true
        viewModel.getPlaylists()
    }

    override fun enableFragmentContainer() {
        binding.PlayerFragmentContainer.isVisible = true
        binding.BottomSheetLayout.root.isVisible = false
    }
}

