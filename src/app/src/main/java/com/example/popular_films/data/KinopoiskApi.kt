package com.example.popular_films.data

import com.example.popular_films.data.remote.responses.FilmInfo
import com.example.popular_films.data.remote.responses.FilmList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
  @GET("films/top")
  @Headers("X-API-KEY: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b", "Content-Type: application/json")
  suspend fun getTopFilms(@Query("type") type: String, @Query("page") page: Int): FilmList

  @GET("films/{kinopoiskId}")
  @Headers("X-API-KEY: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b", "Content-Type: application/json")
  suspend fun getFilmInfo(@Path("kinopoiskId") kinopoiskId: Int): FilmInfo
}