package com.practicum.playlistmaker.data.search.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.search.TracksStorage
import com.practicum.playlistmaker.data.search.models.TrackDto
import java.lang.reflect.Type

class SharedPrefsTracksStorage(context: Context): TracksStorage {

    private val sharedPrefs = context.getSharedPreferences(TRACK_STORAGE_PREFERENCES, Context.MODE_PRIVATE)

    override fun saveHistory(historyList: List<TrackDto>) {
        sharedPrefs.edit()
            .putString(HISTORY_KEY, createJsonFromTrackDtoList(historyList))
            .apply()
    }

    override fun getHistory(): ArrayList<TrackDto>? {
        val restoreTrackList = sharedPrefs.getString(HISTORY_KEY, null)
            ?.let { createTrackDtoListFromJson(it) }

        return restoreTrackList
    }

    override fun cleanHistory() {
        sharedPrefs.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    private fun createJsonFromTrackDtoList(list: List<TrackDto>): String {
        return Gson().toJson(list)
    }

    private fun createTrackDtoListFromJson(jsonValue: String): ArrayList<TrackDto> {
        return Gson().fromJson(jsonValue, trackListType)
    }

    companion object {
        const val HISTORY_KEY = "history_key"
        const val TRACK_STORAGE_PREFERENCES = "track_storage_preferences"
        private val trackListType: Type? = object : TypeToken<ArrayList<TrackDto>>() {}.type
    }
}