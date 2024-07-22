package com.practicum.playlistmaker.domain.settings.api

import com.practicum.playlistmaker.domain.settings.models.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)

    companion object {
        const val NIGHT_MODE_KEY = "night_mode_key"
        const val SETTINGS_STORAGE = "settings_storage"
    }
}