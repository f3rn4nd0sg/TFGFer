package edu.pract5.tfgfer.data

import edu.pract5.tfgfer.model.animeOnAir.AnimeData
import edu.pract5.tfgfer.model.episodio.Episodio
import edu.pract5.tfgfer.model.latestEpisodes.EpisodeData
import edu.pract5.tfgfer.model.serie.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(val remoteDataSource: RemoteDataSource) {

    /**
     * Recupera la lista de animes que están en emisión.
     */
    fun fetchAnimesOnAir(): Flow<List<AnimeData>> {
        return flow {
            val resultAPI = remoteDataSource.getAnimesOnAir()
            emit(resultAPI.data)
        }
    }

    /**
     * Recupera la lista de los últimos episodios lanzados.
     */
    fun fetchLatestEpisodes(): Flow<List<EpisodeData>> {
        return flow {
            val resultAPI = remoteDataSource.getLatestEpisodes()
            emit(resultAPI.data)
        }
    }

    /**
     * Recupera un anime por su slug (nombre de este).
     */
    fun fetchAnimeBySlug(slug: String): Flow<Anime> {
        return flow {
            val resultAPI = remoteDataSource.getAnimeBySlug(slug)
            emit(resultAPI)
        }
    }

    /**
     * Recupera un episodio por su slug (nombre de este) y su número.
     */
    fun fetchEpisodeBySlugAndNumber(slug: String, number: Int): Flow<Episodio> {
        return flow {
            emit(remoteDataSource.getEpisodeBySlugAndNumber(slug, number))
        }
    }

    /**
     * Recupera un episodio por su slug (nombre de este).
     */
    fun fetchEpisodeBySlug(slug: String): Flow<Episodio> {
        return flow {
            emit (remoteDataSource.getEpisodeBySlug(slug))
        }
    }
}