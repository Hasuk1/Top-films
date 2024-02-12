package com.example.popular_films.utility

import com.example.popular_films.data.remote.responses.Country
import com.example.popular_films.data.remote.responses.Genre

fun String.capitalizeFirstLetter(): String {
  val lowerCaseString = this.lowercase()
  return "${lowerCaseString.first().uppercaseChar()}${lowerCaseString.substring(1)}"
}

fun List<Genre>.toGenreList(): String {
  return joinToString { it.genre.capitalizeFirstLetter() }
}

fun List<Country>.toCountryList(): String {
  return joinToString { it.country.capitalizeFirstLetter() }
}