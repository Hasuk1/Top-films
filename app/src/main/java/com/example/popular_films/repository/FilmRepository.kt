package com.example.popular_films.repository

import com.example.popular_films.data.KinopoiskApi
import com.example.popular_films.data.remote.responses.FilmInfo
import com.example.popular_films.data.remote.responses.FilmList
import com.example.popular_films.utility.Response

class FilmRepository(
  private val api: KinopoiskApi
) {
  suspend fun getTopFilms(type: String = "TOP_100_POPULAR_FILMS"): Response<FilmList> {
    val response = try {
      api.getTopFilms(type)
    } catch (e: Exception) {
      return Response.Error("Error get data from API for film list")
    }
    return Response.Success(response)
  }

  suspend fun getFilmInfo(kinopoiskId: Int): Response<FilmInfo> {
    val response = try {
      api.getFilmInfo(kinopoiskId)
    } catch (e: Exception) {
      return Response.Error("Error get data from API for some film info")
    }
    return Response.Success(response)
  }
}