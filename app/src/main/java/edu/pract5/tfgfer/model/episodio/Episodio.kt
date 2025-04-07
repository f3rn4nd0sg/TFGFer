package edu.pract5.tfgfer.model.episodio

import com.google.gson.annotations.SerializedName

data class Episodio(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: Data
)

data class Data(
    @SerializedName("title")
    val title: String,

    @SerializedName("number")
    val number: Int,

    @SerializedName("servers")
    val servers: List<Server>
)

data class Server(
    @SerializedName("name")
    val name: String,

    @SerializedName("download")
    val download: String,

    @SerializedName("embed")
    val embed: String
)
