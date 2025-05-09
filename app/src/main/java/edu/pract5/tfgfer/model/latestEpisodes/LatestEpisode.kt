package edu.pract5.tfgfer.model.latestEpisodes

import com.google.gson.annotations.SerializedName

data class LatestEpisode(
    @SerializedName("data")
    val data: List<LatestEpisodeItem>,
    @SerializedName("success")
    val success: Boolean
)

data class LatestEpisodeItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("url")
    val url: String
)