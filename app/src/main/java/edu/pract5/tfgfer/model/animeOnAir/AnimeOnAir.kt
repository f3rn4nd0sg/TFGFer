package edu.pract5.tfgfer.model.animeOnAir

import com.google.gson.annotations.SerializedName

data class AnimeOnAir(
    @SerializedName("data")
    val data: List<AnimeData>,
    @SerializedName("success")
    val success: Boolean
)

data class AnimeData(
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("url")
    val url: String
)