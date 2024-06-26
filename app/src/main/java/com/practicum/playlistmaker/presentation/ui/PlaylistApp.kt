package com.practicum.playlistmaker.presentation.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.entities.Track

class PlaylistApp: Application() {

    var darkThemeValue = false

    override fun onCreate() {
        super.onCreate()
        val appSharePrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        darkThemeValue = appSharePrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkThemeValue)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkThemeValue = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val THEME_KEY = "theme_key"
        const val TRACK_KEY = "track_key"
        const val APP_PREFERENCES = "app_preferences"
    }
}

fun createJsonFromTrack(track: Track): String {
    return Gson().toJson(track)
}

fun createTrackFromJson(jsonValue: String): Track {
    return Gson().fromJson(jsonValue, Track::class.java)
}