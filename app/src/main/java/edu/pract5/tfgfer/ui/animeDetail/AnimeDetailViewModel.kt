package edu.pract5.tfgfer.ui.animeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.serie.Anime
import kotlinx.coroutines.flow.Flow

class AnimeDetailViewModel(val repository: Repository, val slug: String) : ViewModel() {

    val animeDetail: Flow<Anime>
        get() = repository.fetchAnimeBySlug(slug)
}

@Suppress("UNCHECKED_CAST")
class AnimeDetailViewModelFactory(
    private val repository: Repository,
    private val slug: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnimeDetailViewModel(repository, slug) as T
    }
}


