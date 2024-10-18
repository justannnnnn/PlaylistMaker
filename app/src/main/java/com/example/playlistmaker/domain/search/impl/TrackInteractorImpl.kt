package com.example.playlistmaker.domain.search.impl


import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.data.search.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expr: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(expr)
                if (tracks.isNullOrEmpty()) {
                    consumer.consume(ArrayList())
                } else {
                    consumer.consume(ArrayList(tracks))
                }
            } catch (e: Exception) {
                consumer.onError(e.message ?: "Unknown error")
            }
        }
    }

}