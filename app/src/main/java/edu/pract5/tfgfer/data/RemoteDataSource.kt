package edu.pract5.tfgfer.data

class RemoteDataSource {
    private val api = AnimeAPI.getRetrofit2Api()

    suspend fun getAnimesOnAir() = api.getAnimesOnAir()

    suspend fun getLatestEpisodes() = api.getLatestEpisodes()

    suspend fun getAnimeBySlug(slug: String) = api.getAnimeBySlug(slug)

    suspend fun getEpisodeBySlugAndNumber(slug: String, number: Int) = api.getEpisodeBySlugAndNumber(slug, number)

    suspend fun getEpisodeBySlug(slug: String) = api.getEpisodeBySlug(slug)

    suspend fun searchAnime(query: String) = api.searchAnime(query)

    suspend fun searchByFilter(
        order: String = "default",
        page: Int = 1,
        types: List<String>? = null,
        genres: List<String>? = null,
        statuses: List<Int>? = null
    ) = api.searchByFilter(order, page, types, genres, statuses)

    suspend fun searchByUrl(url: String) = api.searchByUrl(url)


}