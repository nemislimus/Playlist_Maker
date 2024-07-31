package com.practicum.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.data.search.NetworkClient
import com.practicum.playlistmaker.data.search.models.NetworkResponse
import com.practicum.playlistmaker.data.search.models.TracksSearchRequest

class RetrofitNetworkClient (
    private val context: Context,
    private val itunesService: ItunesApiService,
): NetworkClient {

    override fun doRequest(requestModel: Any): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply { resultCode = NetworkClient.NO_CONNECTION_CODE }
        }
        if (requestModel !is TracksSearchRequest) {
            return NetworkResponse().apply { resultCode = NetworkClient.BAD_REQUEST_CODE }
        }

        val response = itunesService.getTracksOnSearch(requestModel.expression).execute()
        val body = response.body()
        return body?.apply { resultCode = response.code() }
            ?: NetworkResponse().apply { resultCode = response.code() }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}