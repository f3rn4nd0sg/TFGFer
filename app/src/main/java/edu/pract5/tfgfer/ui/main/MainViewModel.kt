package edu.pract5.tfgfer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.animeOnAir.AnimeData
import edu.pract5.tfgfer.model.latestEpisodes.LatestEpisodeItem
import kotlinx.coroutines.flow.Flow

class MainViewModel(val repository: Repository) : ViewModel() {
    /*
    val favoriteEpisodes: Flow<List<LatestEpisodeItem>> = repository.fetchFavoriteEpisodes()

     */
    val currentAnimeOnAir: Flow<List<AnimeData>> = repository.fetchAnimesOnAir()
    val currentLatestEpisodes: Flow<List<LatestEpisodeItem>> = repository.fetchLatestEpisodes()
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}