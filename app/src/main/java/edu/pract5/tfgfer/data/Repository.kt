package edu.pract5.tfgfer.data

import edu.pract5.tfgfer.model.animeOnAir.AnimeData
import edu.pract5.tfgfer.model.busqueda.Media
import edu.pract5.tfgfer.model.episodio.Episodio
import edu.pract5.tfgfer.model.latestEpisodes.LatestEpisodeItem
import edu.pract5.tfgfer.model.serie.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(val remoteDataSource: RemoteDataSource) {

    fun fetchAnimesOnAir(): Flow<List<AnimeData>> {
        return flow {
            val resultAPI = remoteDataSource.getAnimesOnAir()
            emit(resultAPI.data)
        }
    }

    fun fetchLatestEpisodes(): Flow<List<LatestEpisodeItem>> {
        return flow {
            val resultAPI = remoteDataSource.getLatestEpisodes()
            emit(resultAPI.data)
        }
    }

    fun fetchAnimeBySlug(slug: String): Flow<Anime> {
        return flow {
            val resultAPI = remoteDataSource.getAnimeBySlug(slug)
            emit(resultAPI)
        }
    }

    fun fetchEpisodeBySlugAndNumber(slug: String, number: Int): Flow<Episodio> {
        return flow {
            emit(remoteDataSource.getEpisodeBySlugAndNumber(slug, number))
        }
    }

    fun fetchEpisodeBySlug(slug: String): Flow<Episodio> {
        return flow {
            emit(remoteDataSource.getEpisodeBySlug(slug))
        }
    }

    fun searchByUrl(url: String): Flow<List<Media>> {
        return flow {
            val resultAPI = remoteDataSource.searchByUrl(url)
            emit(resultAPI.data.media)
        }
    }

    fun searchAnime(query: String): Flow<List<Media>> {
        return flow {
            val resultAPI = remoteDataSource.searchAnime(query)
            emit(resultAPI.data.media)
        }
    }


    fun searchByFilter(
        order: String = "default",
        page: Int = 1,
        types: List<String>? = null,
        genres: List<String>? = null,
        statuses: List<Int>? = null
    ): Flow<List<Media>> {
        return flow {
            val resultAPI = remoteDataSource.searchByFilter(order, page, types, genres, statuses)
            emit(resultAPI.data.media)
        }
    }
    /*
    fun fetchFavoriteEpisodes(): Flow<List<LatestEpisodeItem>> {
        return flow {
            val resultAPI = LocalDataSource.getFavoriteEpisodes()
            //TODO
            emit(resultAPI as List<LatestEpisodeItem>)
        }
    }
     */

}