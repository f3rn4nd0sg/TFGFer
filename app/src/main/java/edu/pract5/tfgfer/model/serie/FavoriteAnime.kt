package edu.pract5.tfgfer.model.serie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_animes")
data class FavoriteAnime(
    @PrimaryKey
    val slug: String,
    val title: String,
    val status: String,
    val type: String,
    val cover: String,
    val synopsis: String
)

// Función de extensión para convertir de AnimeData a FavoriteAnime
fun AnimeData.toFavoriteAnime(slug: String): FavoriteAnime {
    return FavoriteAnime(
        slug = slug,
        title = this.title,
        status = this.status,
        type = this.type,
        cover = this.cover,
        synopsis = this.synopsis
    )
}