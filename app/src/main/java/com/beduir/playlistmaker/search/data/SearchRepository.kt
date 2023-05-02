package com.beduir.playlistmaker.search.data

import com.beduir.playlistmaker.TracksResponse
import com.beduir.playlistmaker.search.domain.Track
import com.beduir.playlistmaker.iTunesAPI
import com.beduir.playlistmaker.search.domain.ISearchRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository (
    private val api: iTunesAPI
): ISearchRepository {

    override fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit) {
        api
            .search(query)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        onSuccess.invoke(response.body()?.results ?: emptyList())
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }
}