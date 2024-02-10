package com.example.popular_films

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KinopoiskApi {
  @GET("/api/v2.2/films/top")
  @Headers("X-API-KEY: 619d3c66-3242-4139-9331-e63292c7e233", "Content-Type: application/json")
  suspend fun getTopFilms(@Query("type") type: String = "TOP_100_POPULAR_FILMS"): TopFilmsResponse
}
