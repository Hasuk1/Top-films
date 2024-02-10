package com.example.popular_films

data class FilmInfo(
  val kinopoiskId:Int,
  val nameRu:String,
  val posterUrl:String,
  val year:Int,
  val genres: List<Genre>,
)
