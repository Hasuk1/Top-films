package com.example.popular_films.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.graphics.Color
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
import com.example.popular_films.ui.theme.Blue
import com.example.popular_films.ui.theme.LightBlue
import com.example.popular_films.utility.Constants
import com.example.popular_films.utility.MenuList
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
  val favoriteFilmList by remember {
    mutableStateOf(mutableStateListOf<FilmInfo>())
  }
  val isNetworkAvailable = isNetworkAvailable(context)
  val ethernetStatus =
    remember { mutableStateOf(if (isNetworkAvailable) "OK" else "Нет доступа в интернет") }
  val isLoading = remember { mutableStateOf(isNetworkAvailable) }
  val whichList = remember { mutableStateOf(MenuList.POPULAR) }
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
  RenderScreen(navController, isLoading, ethernetStatus, whichList, topFilmList, favoriteFilmList)
}

@Composable
private fun RenderScreen(
  navController: NavController,
  isLoading: MutableState<Boolean>,
  ethernetStatus: MutableState<String>,
  whichList: MutableState<MenuList>,
  topFilmList: SnapshotStateList<FilmInfo>,
  favoriteFilmList: SnapshotStateList<FilmInfo>
) {
  Surface(
    color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      if (isLoading.value) {
        Shimmer()
      } else {
        TitleListScreen(navController, whichList)
        if (ethernetStatus.value == "OK") {
          when (whichList.value) {
            MenuList.POPULAR -> PopularFilmList(
              navController, topFilmList, favoriteFilmList
            )
            MenuList.FAVORITE -> FavoriteFilmList(navController, favoriteFilmList)
          }
        } else {
          NoInternet(ethernetStatus)
        }
        NavigationButton(whichList)
      }
    }
  }
}

@Composable
private fun TitleListScreen(navController: NavController, whichList: MutableState<MenuList>) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = if (whichList.value == MenuList.POPULAR) "Популярные" else "Избранное",
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
          navController.navigate("FilmSearchScreen") {
            launchSingleTop = true
          }
        })
  }
}

@Composable
private fun FavoriteFilmList(
  navController: NavController, favoriteFilmList: SnapshotStateList<FilmInfo>
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.9F)
      .padding(10.dp)
  ) {
    items(items = favoriteFilmList) { film ->
      FilmCard(navController, film, favoriteFilmList)
    }
  }
}

@Composable
private fun PopularFilmList(
  navController: NavController,
  topFilmList: SnapshotStateList<FilmInfo>,
  favoriteFilmList: SnapshotStateList<FilmInfo>
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.9F)
      .padding(10.dp)
  ) {
    items(items = topFilmList) { film ->
      FilmCard(navController, film, favoriteFilmList)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilmCard(
  navController: NavController, film: FilmInfo, favoriteFilmList: SnapshotStateList<FilmInfo>
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
      .padding(5.dp)
      .combinedClickable(onClick = {
        navController.navigate("FilmDetailScreen/${film.filmId}") {
          launchSingleTop = true
        }
      }, onLongClick = {
        if (film in favoriteFilmList) {
          favoriteFilmList.remove(film)
        } else {
          favoriteFilmList.add(film)
        }
      }), shape = RoundedCornerShape(10.dp), elevation = 5.dp
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
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
      if (film in favoriteFilmList) {
        Image(
          modifier = Modifier
            .padding(15.dp)
            .size(20.dp)
            .align(Alignment.TopEnd),
          painter = painterResource(id = R.drawable.heart),
          contentDescription = "favorite"
        )
      }
    }
  }
}

@Composable
fun NavigationButton(whichList: MutableState<MenuList>) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp)
  ) {
    Button(modifier = Modifier
      .fillMaxWidth(0.5F)
      .fillMaxHeight()
      .padding(10.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = if (whichList.value == MenuList.POPULAR) LightBlue else Blue),
      shape = RoundedCornerShape(20.dp),
      onClick = { whichList.value = MenuList.POPULAR }) {
      Text(
        text = "Популярные", fontSize = 17.sp,
        color = if (whichList.value == MenuList.FAVORITE) Color.White else Blue,
      )
    }
    Button(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(10.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = if (whichList.value == MenuList.FAVORITE) LightBlue else Blue),
      shape = RoundedCornerShape(20.dp),
      onClick = { whichList.value = MenuList.FAVORITE }) {
      Text(
        text = "Избранное", fontSize = 17.sp,
        color = if (whichList.value == MenuList.FAVORITE) Blue else Color.White,
      )
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
    for (i in 1..5) {
      val topFilmsResponse = withContext(Dispatchers.IO) {
        kinopoiskApi.getTopFilms("TOP_100_POPULAR_FILMS", i)
      }
      topFilmsResponse.films.let {
        topFilmList.addAll(it)
      }
    }
    isLoading.value = false
  } catch (e: Exception) {
    isLoading.value = false
    ethernetStatus.value = "Ошибка загрузки..."
    Log.d("MyLog", "ApiLog = $e")
  }
}