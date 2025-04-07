package edu.pract5.tfgfer.data

import edu.pract5.tfgfer.model.animeOnAir.AnimeOnAir
import edu.pract5.tfgfer.model.episodio.Episodio
import edu.pract5.tfgfer.model.latestEpisodes.LatestEpisode
import edu.pract5.tfgfer.model.serie.Anime
import edu.pract5.tfgfer.model.serie.Episode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class AnimeAPI {
    companion object {
        const val BASE_URL = "https://animeflv.ahmedrangel.com/"

        fun getRetrofit2Api(): AnimeAPIInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(AnimeAPIInterface::class.java)
        }
    }
}

interface AnimeAPIInterface {
    @GET("api/list/latest-episodes")
    suspend fun getLatestEpisodes(): LatestEpisode

    @GET("api/list/animes-on-air")
    suspend fun getAnimesOnAir(): AnimeOnAir

    @GET("api/anime/{slug}")
    suspend fun getAnimeBySlug(@Path("slug") slug: String): Anime

    @GET("api/anime/{slug}/episode/{number}")
    suspend fun getEpisodeBySlugAndNumber(@Path("slug") slug: String, @Path("number") number: Int): Episodio

    @GET("api/anime/episode/{slug}")
    suspend fun getEpisodeBySlug(@Path("slug") slug: String): Episodio
}