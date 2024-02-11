package com.example.popular_films.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.popular_films.R
import com.example.popular_films.data.KinopoiskApi
import com.example.popular_films.data.remote.responses.FilmInfo
import com.example.popular_films.ui.NoInternet
import com.example.popular_films.ui.Shimmer
import com.example.popular_films.utility.Constants
import com.example.popular_films.utility.isNetworkAvailable
import com.example.popular_films.utility.toCountryList
import com.example.popular_films.utility.toGenreList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun FilmDetailScreen(navController: NavController, context: Context, kinopoiskId: Int? = 754) {
  val filmInfo = remember { mutableStateOf<FilmInfo?>(null) }
  val isNetworkAvailable = isNetworkAvailable(context)
  val ethernetStatus =
    remember { mutableStateOf(if (isNetworkAvailable) "OK" else "Нет доступа в интернет") }
  val isLoading = remember { mutableStateOf(isNetworkAvailable) }
  val kinopoiskApi = Retrofit.Builder().baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create()).build().create(KinopoiskApi::class.java)
  DisposableEffect(true) {
    val job = CoroutineScope(Dispatchers.Main).launch {
      loadFilmInfo(kinopoiskApi, filmInfo, isLoading, ethernetStatus, kinopoiskId!!)
    }
    onDispose {
      job.cancel()
    }
  }
  RenderScreen(navController, isLoading, ethernetStatus, filmInfo)
}

@Composable
private fun RenderScreen(
  navController: NavController,
  isLoading: MutableState<Boolean>,
  ethernetStatus: MutableState<String>,
  filmInfo: MutableState<FilmInfo?>
) {
  Surface(
    color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      if (isLoading.value) {
        Shimmer()
      } else {
        if (ethernetStatus.value == "OK") {
          filmInfo.value?.let { FilmDetail(navController, it) } ?: NoInternet(ethernetStatus)
        } else {
          NoInternet(ethernetStatus)
        }
      }
    }
  }
}

@Composable
private fun FilmDetail(navController: NavController, film: FilmInfo) {
  Column(
    modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.62F)
    ) {
      Image(
        painter = rememberImagePainter(data = film.posterUrl),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillWidth,
        contentDescription = film.nameRu,
      )
      Image(painter = painterResource(id = R.drawable.backarrow),
        contentDescription = "back_arrow",
        modifier = Modifier
          .padding(15.dp)
          .size(35.dp)
          .clickable {
            navController.popBackStack("FilmDetailScreen/${film.filmId}", inclusive = true)
            navController.navigate("PopularFilmListScreen")
          })
    }

    Text(
      text = film.nameRu,
      fontSize = 25.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    )
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.53F),
    ) {
      item {
        Text(
          text = film.description, modifier = Modifier.padding(horizontal = 20.dp), fontSize = 15.sp
        )
      }
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp),
      verticalAlignment = Alignment.Bottom
    ) {
      Text(text = "Жанры: ", fontSize = 17.sp, fontWeight = FontWeight.Bold)
      Text(text = film.genres.toGenreList(), fontSize = 15.sp)
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      verticalAlignment = Alignment.Bottom
    ) {
      Text(text = "Страны: ", fontSize = 17.sp, fontWeight = FontWeight.Bold)
      Text(text = film.countries.toCountryList(), fontSize = 15.sp)
    }
  }
}

private suspend fun loadFilmInfo(
  kinopoiskApi: KinopoiskApi,
  filmInfo: MutableState<FilmInfo?>,
  isLoading: MutableState<Boolean>,
  ethernetStatus: MutableState<String>,
  kinopoiskId: Int
) {

  try {
    val filmResponse = withContext(Dispatchers.IO) {
      kinopoiskApi.getFilmInfo(kinopoiskId)
    }
    filmResponse.let {
      filmInfo.value = filmResponse
    }
    isLoading.value = false
  } catch (e: Exception) {
    isLoading.value = false
    ethernetStatus.value = "Ошибка загрузки..."
    Log.d("MyLog", "ApiLog = $e")
  }
}