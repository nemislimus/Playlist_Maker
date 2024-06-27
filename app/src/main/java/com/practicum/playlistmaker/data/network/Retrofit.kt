package com.practicum.playlistmaker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private const val ITUNES_BASE_URL = "https://itunes.apple.com"

    val itunesInstance: ItunesApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ItunesApiService::class.java)
    }
}