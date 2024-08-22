package com.practicum.playlistmaker.ui

import android.app.Application
import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.presentationModule
import com.practicum.playlistmaker.domain.search.models.Track
import com.practicum.playlistmaker.domain.settings.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistApp : Application() {

    var darkThemeValue = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PlaylistApp)
            modules(dataModule, domainModule, presentationModule)
        }

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
        context.resources.displayMetrics
    ).toInt()
}

fun convertTimeValueFromLongToString(timeValueLong: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeValueLong)
}