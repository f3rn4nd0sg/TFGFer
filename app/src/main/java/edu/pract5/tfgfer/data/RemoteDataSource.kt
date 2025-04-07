package edu.pract5.tfgfer.data

class RemoteDataSource {
    private val api = AnimeAPI.getRetrofit2Api()

    suspend fun getAnimesOnAir() = api.getAnimesOnAir()

    suspend fun getLatestEpisodes() = api.getLatestEpisodes()

    suspend fun getAnimeBySlug(slug: String) = api.getAnimeBySlug(slug)

    suspend fun getEpisodeBySlugAndNumber(slug: String, number: Int) = api.getEpisodeBySlugAndNumber(slug, number)

    suspend fun getEpisodeBySlug(slug: String) = api.getEpisodeBySlug(slug)
}