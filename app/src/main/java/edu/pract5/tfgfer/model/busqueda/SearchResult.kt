package edu.pract5.tfgfer.model.busqueda

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
)

data class Data(
    @SerializedName("media")
    val media: List<Media>
)

data class Media(
    @SerializedName("title") val title: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String
)


