package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.http.*

interface ItunesApiService {
    @GET("search")
    fun getTracksOnSearch(
        @Query("term") text: String,
        @Query("entity") entity:String = "song"
    ) : Call<TracksResponse>
}