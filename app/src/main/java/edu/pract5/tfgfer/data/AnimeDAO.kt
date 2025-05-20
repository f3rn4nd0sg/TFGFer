package edu.pract5.tfgfer.data

import androidx.room.*
import edu.pract5.tfgfer.model.serie.FavoriteAnime
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM favorite_animes")
    fun getAllFavorites(): Flow<List<FavoriteAnime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(anime: FavoriteAnime)

    @Query("DELETE FROM favorite_animes WHERE slug = :slug")
    suspend fun deleteFavoriteBySlug(slug: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_animes WHERE slug = :slug)")
    fun isAnimeFavorite(slug: String): Flow<Boolean>
}