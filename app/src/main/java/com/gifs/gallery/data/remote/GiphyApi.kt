package com.gifs.gallery.data.remote

import com.gifs.gallery.data.remote.dto.GifsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {
    companion object {
        const val BASE_URL = "https://api.giphy.com/v1/"

        /**
         * This api key has a quota, no more 100 requests per hour to the api
         */
        const val API_KEY = "v3nKlncYQHubdI6iwvReVSL65zuEiP5j"
        const val BASE_LIMIT = 30
    }

    /**
     * limit - amount of elements loading per page
     * offset - in other words number of page
     */

    @GET("gifs/trending")
    suspend fun getTrendingGifs(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = BASE_LIMIT,
        @Query("api_key") apiKey: String = API_KEY
    ): GifsResponse

    @GET("gifs/search")
    suspend fun searchGifs(
        @Query("q") searchQuery: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = BASE_LIMIT,
        @Query("api_key") apiKey: String = API_KEY
    ): GifsResponse
}