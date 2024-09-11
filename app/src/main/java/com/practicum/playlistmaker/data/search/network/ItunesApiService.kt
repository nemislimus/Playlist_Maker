package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.models.TracksSearchResponse
import retrofit2.http.*

interface ItunesApiService {
    @GET("search")
    suspend fun getTracksOnSearch(
        @Query("term") text: String,
        @Query("entity") entity:String = "song"
    ) : TracksSearchResponse
}