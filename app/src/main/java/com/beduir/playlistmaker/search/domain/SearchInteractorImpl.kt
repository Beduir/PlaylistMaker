package com.beduir.playlistmaker.search.domain

import com.beduir.playlistmaker.search.domain.models.Track
import com.beduir.playlistmaker.util.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val trackHistory: TrackHistory,
    private val repository: SearchRepository
) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun clearHistory() {
        trackHistory.clearHistory()
    }

    override fun getHistory(): ArrayList<Track> {
        return trackHistory.getHistory()
    }

    override fun addTrack(track: Track) {
        trackHistory.addTrack(track)
    }

    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}