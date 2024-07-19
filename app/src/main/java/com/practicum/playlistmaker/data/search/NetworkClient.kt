package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.search.models.NetworkResponse

interface NetworkClient {
    fun doRequest(requestModel: Any): NetworkResponse
}