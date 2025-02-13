package com.example.playlistmaker.ui.media.favorites.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.ui.media.favorites.model.FavoritesState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val favoritesStateLiveData = MutableLiveData<FavoritesState>(FavoritesState.isEmpty)
    fun observeFavoritesState(): LiveData<FavoritesState> = favoritesStateLiveData

    fun getFavoritesData(){
        viewModelScope.launch {
            favoritesInteractor.getFavoritesTracks()
                .collect{
                    if (it.isEmpty().not()) favoritesStateLiveData.postValue(FavoritesState.Content(it))
                    else favoritesStateLiveData.postValue(FavoritesState.isEmpty)
                }
        }
    }
}