package com.example.popular_films

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.popular_films.screens.FilmDetailScreen
import com.example.popular_films.screens.FilmListScreen
import com.example.popular_films.screens.FilmSearchScreen
import com.example.popular_films.ui.theme.PopularFilmsTheme

class MainActivity : ComponentActivity() {
  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PopularFilmsTheme {
        val navController = rememberNavController()
        NavHost(
          navController = navController, startDestination = "PopularFilmListScreen"
        ) {
          composable("PopularFilmListScreen") {
            FilmListScreen(navController, this@MainActivity)
          }
          composable("FilmSearchScreen") {
            FilmSearchScreen(navController)
          }
          composable(
            "FilmDetailScreen/{kinopoiskId}",
            arguments = listOf(navArgument("kinopoiskId") { type = NavType.IntType })
          ) { backStackEntry ->
            val kinopoiskId = backStackEntry.arguments?.getInt("kinopoiskId")
            FilmDetailScreen(navController, this@MainActivity, kinopoiskId)
          }
        }
      }
    }
  }
}
