package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var backToSearchButton: Toolbar
    private lateinit var albumCover: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistTitle: TextView

    private lateinit var playButton: ImageView
    private lateinit var addToPlaylist: ImageView
    private lateinit var addToFavorite: ImageView
    private lateinit var trackTimer: TextView

    private lateinit var trackDuration: TextView
    private lateinit var trackDurationValue: TextView
    private lateinit var albumLine: Group
    private lateinit var albumValue: TextView
    private lateinit var releaseDate: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var genre: TextView
    private lateinit var genreValue: TextView
    private lateinit var country: TextView
    private lateinit var countryValue: TextView

    private var hasCollection: Boolean = true

    // Вот это по хорошему надо к Track привязать, но пока тут оставил
    private var trackOnAir: Boolean = false
    private var trackInPlaylist: Boolean = false
    private var trackInFavorites: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val currentTrack = intent.getStringExtra(TRACK_KEY)?.let { createTrackFromJson(it) }

        backToSearchButton = findViewById(R.id.tbBackFromPlayerButton)
        albumCover = findViewById(R.id.ivAlbumCover)
        trackTitle = findViewById(R.id.tvTrackNamePlayer)
        artistTitle = findViewById(R.id.tvArtistNamePlayer)

        playButton = findViewById(R.id.ivPlayButton)
        addToPlaylist = findViewById(R.id.ivAddToPlaylist)
        addToFavorite = findViewById(R.id.ivAddToFavorites)
        trackTimer = findViewById(R.id.tvTrackTimer)

        trackDuration = findViewById(R.id.tvTrackDuration)
        trackDurationValue = findViewById(R.id.tvTrackDurationValue)
        albumLine = findViewById(R.id.albumGroup)
        albumValue = findViewById(R.id.tvTrackAlbumValue)
        releaseDate = findViewById(R.id.tvTrackReleaseDate)
        releaseDateValue = findViewById(R.id.tvTrackReleaseDateValue)
        genre = findViewById(R.id.tvTrackGenre)
        genreValue = findViewById(R.id.tvTrackGenreValue)
        country = findViewById(R.id.tvTrackCountry)
        countryValue = findViewById(R.id.tvTrackCountryValue)

        // Кнопка "назад к поиску"
        backToSearchButton.setOnClickListener {
            finish()
        }

        // Проверяем наличие названия альбома
        if (currentTrack?.collectionName.isNullOrEmpty()) {
            hasCollection = false
            albumLine.visibility = View.GONE
        }

        //Наполняем UI данными из currentTrack
        if (currentTrack != null) {
            Glide.with(this)
                .load(getBigCoverArtwork(currentTrack.artworkUrl100))
                .placeholder(R.drawable.ic_placeholder_track_image)
                .centerCrop()
                .into(albumCover)

            if (hasCollection) albumValue.text = currentTrack.collectionName

            trackTitle.text = currentTrack.trackName
            artistTitle.text = currentTrack.artistName
            trackDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis)
            trackTimer.text = trackDurationValue.text
            releaseDateValue.text = currentTrack.releaseDate.substring(0, 4)
            genreValue.text = currentTrack.primaryGenreName
            countryValue.text = currentTrack.country
        }

        // Нажатие на Play
        playButton.setOnClickListener {
            if (trackOnAir) {
                trackOnAir = false
                playButton.setImageResource(R.drawable.play_button)
            } else {
                trackOnAir = true
                playButton.setImageResource(R.drawable.pause_button)
            }
        }

        // Нажатие на addToPlaylist
        addToPlaylist.setOnClickListener {
            if (trackInPlaylist) {
                trackInPlaylist = false
                addToPlaylist.setImageResource(R.drawable.add_to_playlist)
            } else {
                trackInPlaylist = true
                addToPlaylist.setImageResource(R.drawable.added_in_playlist)
            }
        }

        // Нажатие на addToFavorites
        addToFavorite.setOnClickListener {
            if (trackInFavorites) {
                trackInFavorites = false
                addToFavorite.setImageResource(R.drawable.add_to_favorite)
            } else {
                trackInFavorites = true
                addToFavorite.setImageResource(R.drawable.added_in_favorites)
            }
        }
    }

    private fun getBigCoverArtwork(link: String) = link.replaceAfterLast('/', "512x512bb.jpg")

}

