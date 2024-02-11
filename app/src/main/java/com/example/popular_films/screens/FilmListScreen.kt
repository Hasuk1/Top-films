package com.example.popular_films.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.popular_films.utility.toGenreList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun FilmListScreen(navController: NavController, context: Context) {
  val topFilmList by remember {
    mutableStateOf(mutableStateListOf<FilmInfo>())
  }
  val isNetworkAvailable = isNetworkAvailable(context)
  val ethernetStatus =
    remember { mutableStateOf(if (isNetworkAvailable) "OK" else "Нет доступа в интернет") }
  val isLoading = remember { mutableStateOf(isNetworkAvailable) }
  val kinopoiskApi = Retrofit.Builder().baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create()).build().create(KinopoiskApi::class.java)

  if (isNetworkAvailable) {
    DisposableEffect(true) {
      val job = CoroutineScope(Dispatchers.Main).launch {
        loadTopFilms(kinopoiskApi, topFilmList, isLoading, ethernetStatus)
      }
      onDispose {
        job.cancel()
      }
    }
  }
  RenderScreen(navController, isLoading, ethernetStatus, topFilmList)
}

@Composable
private fun FilmList(navController: NavController, topFilmList: SnapshotStateList<FilmInfo>) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(10.dp)
  ) {
    items(items = topFilmList) { film ->
      FilmCard(navController, film)
    }
  }
}

@Composable
private fun FilmCard(navController: NavController, film: FilmInfo) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
      .padding(5.dp)
      .clickable {
        navController.navigate("FilmDetailScreen/${film.filmId}") {
          popUpTo("PopularFilmListScreen") { inclusive = true }
          launchSingleTop = true
        }
      }, shape = RoundedCornerShape(10.dp), elevation = 5.dp
  ) {
    Row(modifier = Modifier.padding(10.dp)) {
      Image(
        painter = rememberImagePainter(data = film.posterUrlPreview), modifier = Modifier.clip(
          MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10.dp),
            topStart = CornerSize(10.dp),
            bottomEnd = CornerSize(10.dp),
            bottomStart = CornerSize(10.dp)
          )
        ), contentDescription = film.nameRu
      )
      Column(modifier = Modifier.padding(15.dp)) {
        Text(
          text = film.nameRu,
          fontSize = 20.sp,
        )
        Text(
          text = "${film.genres.toGenreList()} (${film.year})"
        )
      }
    }
  }
}

@Composable
private fun TitleListScreen() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "Популярные",
      fontSize = 27.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.weight(1f)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Image(painter = painterResource(id = R.drawable.find),
      contentDescription = "find",
      modifier = Modifier
        .size(35.dp)
        .clickable {

        })
  }
}

@Composable
private fun RenderScreen(
  navController: NavController,
  isLoading: MutableState<Boolean>,
  ethernetStatus: MutableState<String>,
  topFilmList: SnapshotStateList<FilmInfo>
) {
  Surface(
    color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      if (isLoading.value) {
        Shimmer()
      } else {
        TitleListScreen()
        if (ethernetStatus.value == "OK") {
          FilmList(navController, topFilmList)
        } else {
          NoInternet(ethernetStatus)
        }
      }
    }
  }
}

private suspend fun loadTopFilms(
  kinopoiskApi: KinopoiskApi,
  topFilmList: SnapshotStateList<FilmInfo>,
  isLoading: MutableState<Boolean>,
  ethernetStatus: MutableState<String>
) {
  try {
    val topFilmsResponse = withContext(Dispatchers.IO) {
      kinopoiskApi.getTopFilms("TOP_100_POPULAR_FILMS")
    }
    topFilmsResponse.films.let {
      topFilmList.clear()
      topFilmList.addAll(it)
    }
    isLoading.value = false
  } catch (e: Exception) {
    isLoading.value = false
    ethernetStatus.value = "Ошибка загрузки..."
    Log.d("MyLog", "ApiLog = $e")
  }
}