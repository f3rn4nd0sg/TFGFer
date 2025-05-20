package edu.pract5.tfgfer.data

import edu.pract5.tfgfer.model.serie.Anime
import edu.pract5.tfgfer.model.serie.FavoriteAnime
import edu.pract5.tfgfer.model.serie.toFavoriteAnime
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val animeDatabase: AnimeDatabase) {

    fun getFavoriteAnimes(): Flow<List<FavoriteAnime>> {
        return animeDatabase.animeDao().getAllFavorites()
    }

    suspend fun addFavorite(anime: Anime, slug: String) {
        val favoriteAnime = anime.data.toFavoriteAnime(slug)
        animeDatabase.animeDao().insertFavorite(favoriteAnime)
    }

    suspend fun removeFavorite(slug: String) {
        animeDatabase.animeDao().deleteFavoriteBySlug(slug)
    }

    fun isAnimeFavorite(slug: String): Flow<Boolean> {
        return animeDatabase.animeDao().isAnimeFavorite(slug)
    }
}