package com.practicum.playlistmaker.domain.search.models

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)

    companion object {
        const val CHECK_CONNECTION = "Check the connections!"
        const val SERVER_ERROR = "Server error"
    }
}
