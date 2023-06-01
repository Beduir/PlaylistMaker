package com.beduir.playlistmaker.search.data

import com.beduir.playlistmaker.search.data.dto.TracksSearchRequest
import com.beduir.playlistmaker.search.data.dto.TracksSearchResponse
import com.beduir.playlistmaker.search.domain.SearchRepository
import com.beduir.playlistmaker.search.domain.models.Track
import com.beduir.playlistmaker.util.Resource

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}