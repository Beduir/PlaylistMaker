package com.beduir.playlistmaker.search.data.network

import com.beduir.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesAPI {
    @GET("/search")
    fun search(@Query("term") text: String): Call<TracksSearchResponse>
}