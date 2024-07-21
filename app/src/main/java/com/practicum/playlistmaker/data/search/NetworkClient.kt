package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.search.models.NetworkResponse

interface NetworkClient {
    fun doRequest(requestModel: Any): NetworkResponse

    companion object{
        const val BAD_REQUEST_CODE = 400
        const val SUCCESS_CODE = 200
        const val NO_CONNECTION_CODE = -1

    }
}