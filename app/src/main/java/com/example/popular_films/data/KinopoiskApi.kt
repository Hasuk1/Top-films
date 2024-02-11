package com.example.popular_films.data

import com.example.popular_films.data.remote.responses.FilmInfo
import com.example.popular_films.data.remote.responses.FilmList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
  @GET("films/top")
  @Headers("X-API-KEY: 619d3c66-3242-4139-9331-e63292c7e233", "Content-Type: application/json")
  suspend fun getTopFilms(@Query("type") type: String): FilmList

  @GET("films/{kinopoiskId}")
  @Headers("X-API-KEY: 619d3c66-3242-4139-9331-e63292c7e233", "Content-Type: application/json")
  suspend fun getFilmInfo(@Path("kinopoiskId") kinopoiskId: Int): FilmInfo
}