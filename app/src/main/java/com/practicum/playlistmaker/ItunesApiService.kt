package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.*

interface ItunesApiService {
    @GET("search")
    fun getTracksOnSearch(
        @Query("term") text: String,
        @Query("media") media: String = "music",
        @Query("entity") entity:String = "song"
    ) : Call<TracksResponse>
}