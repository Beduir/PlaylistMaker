package com.beduir.playlistmaker.search.data

import com.beduir.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}