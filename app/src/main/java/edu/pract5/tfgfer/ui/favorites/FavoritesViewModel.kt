package edu.pract5.tfgfer.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.serie.FavoriteAnime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    val favoriteAnimes: StateFlow<List<FavoriteAnime>> = repository.getFavoriteAnimes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun removeFavorite(slug: String) {
        viewModelScope.launch {
            repository.removeAnimeFromFavorites(slug)
        }
    }
}

class FavoritesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}