package edu.pract5.tfgfer.ui.episodeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.pract5.tfgfer.data.Repository
import edu.pract5.tfgfer.model.episodio.Episodio
import kotlinx.coroutines.flow.Flow

class EpisodeDetailViewModel(
    val repository: Repository,
    val slug: String,
    val number: String
) : ViewModel() {
    // Mantener compatibilidad
    val episodeDetail: Flow<Episodio> = repository.fetchEpisodeBySlugAndNumber(slug, number.toInt())

}

@Suppress("UNCHECKED_CAST")
class EpisodeDetailViewModelFactory(
    private val repository: Repository,
    private val slug: String,
    private val number: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EpisodeDetailViewModel(repository, slug, number) as T
    }
}