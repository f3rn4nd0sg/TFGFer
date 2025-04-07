package edu.pract5.tfgfer.model.serie

//TODO meter lo del parcelable (la referencia en el gradle)
import com.google.gson.annotations.SerializedName
data class Anime(
    val success: Boolean,
    val data: AnimeData
)

data class AnimeData(
    val title: String,
    @SerializedName("alternative_titles") val alternativeTitles: List<String>,
    val status: String,
    val rating: String,
    val type: String,
    val cover: String,
    val synopsis: String,
    val genres: List<String>,
    val episodes: List<Episode>,
    val url: String
)

data class Episode(
    val number: Int,
    val slug: String,
    val url: String
)
