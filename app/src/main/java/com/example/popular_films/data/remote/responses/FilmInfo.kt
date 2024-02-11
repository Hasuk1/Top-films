package com.example.popular_films.data.remote.responses

data class FilmInfo(
  val kinopoiskId: Int,
  val nameRu: String,
  val posterUrl: String,
  val posterUrlPreview: String,
  val year: Int,
  val description: String,
  val countries: List<Country>,
  val genres: List<Genre>
)