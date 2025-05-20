package edu.pract5.tfgfer.ui.animeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.serie.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimeDetailViewModel(val repository: Repository, val slug: String) : ViewModel() {

    val animeDetail: Flow<Anime>
        get() = repository.fetchAnimeBySlug(slug)

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _anime = MutableStateFlow<Anime?>(null)

    init {
        viewModelScope.launch {
            animeDetail.collectLatest {
                _anime.value = it
                checkFavoriteStatus()
            }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            repository.isAnimeFavorite(slug).collectLatest {
                _isFavorite.value = it
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val anime = _anime.value ?: return@launch

            if (_isFavorite.value) {
                repository.removeAnimeFromFavorites(slug)
            } else {
                repository.addAnimeToFavorites(anime, slug)
            }
        }
    }
}
@Suppress("UNCHECKED_CAST")
class AnimeDetailViewModelFactory(
    private val repository: Repository,
    private val slug: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeDetailViewModel::class.java)) {
            return AnimeDetailViewModel(repository, slug) as T
        }
        return AnimeDetailViewModel(repository, slug) as T
    }
}

