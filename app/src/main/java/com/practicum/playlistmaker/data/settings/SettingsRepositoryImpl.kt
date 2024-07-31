package com.practicum.playlistmaker.data.settings

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.settings.api.SettingsRepository
import com.practicum.playlistmaker.domain.settings.models.ThemeSettings

class SettingsRepositoryImpl(
    private val settingsSharedPrefs: SharedPreferences
): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(settingsSharedPrefs.getBoolean(SettingsRepository.NIGHT_MODE_KEY,false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsSharedPrefs.edit()
            .putBoolean(SettingsRepository.NIGHT_MODE_KEY, settings.nightModeOn)
            .apply()
    }

}