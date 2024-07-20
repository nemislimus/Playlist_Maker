package com.practicum.playlistmaker.ui

import android.app.Application
import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.settings.api.SettingsRepository

class PlaylistApp: Application() {

    var darkThemeValue = false

    override fun onCreate() {
        super.onCreate()
        val appSharePrefs = getSharedPreferences(SettingsRepository.SETTINGS_STORAGE, MODE_PRIVATE)
        darkThemeValue = appSharePrefs.getBoolean(SettingsRepository.NIGHT_MODE_KEY, false)
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
        const val TRACK_KEY = "track_key"
    }
}

fun createJsonFromTrack(track: Track): String {
    return Gson().toJson(track)
}

fun createTrackFromJson(jsonValue: String): Track {
    return Gson().fromJson(jsonValue, Track::class.java)
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}