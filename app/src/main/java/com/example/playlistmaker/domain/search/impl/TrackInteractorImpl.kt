package com.example.playlistmaker.domain.search.impl


import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expr: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expr).map { result ->
            if (result == null) Pair(null, "error")
            else if (result.isEmpty()) Pair(ArrayList(), null)
            else Pair(ArrayList(result), null)
        }
    }
}