package com.example.popular_films

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.popular_films.ui.theme.Blue
import com.example.popular_films.ui.theme.PopularFilmsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PopularFilmsTheme {
        FilmList(this@MainActivity)
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.M)
fun isNetworkAvailable(context: Context): Boolean {
  val connectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork
  val capabilities = connectivityManager.getNetworkCapabilities(network)

  return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun FilmList(context: Context) {
  val topFilmList by remember {
    mutableStateOf(mutableStateListOf<FilmInfo>())
  }
  val isNetworkAvailable = isNetworkAvailable(context)
  val ethernetStatus =
    remember { mutableStateOf(if (isNetworkAvailable) "OK" else "Нет доступа в интернет") }
  val isLoading = remember { mutableStateOf(isNetworkAvailable) }
  val kinopoiskApi = Retrofit.Builder().baseUrl("https://kinopoiskapiunofficial.tech")
    .addConverterFactory(GsonConverterFactory.create()).build().create(KinopoiskApi::class.java)
  if (isNetworkAvailable) {
    DisposableEffect(true) {
      val job = CoroutineScope(Dispatchers.Main).launch {

        try {
          val topFilmsResponse = withContext(Dispatchers.IO) {
            kinopoiskApi.getTopFilms()
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
      onDispose {
        job.cancel()
      }
    }
  }
  if (isLoading.value) {
    Column(modifier = Modifier.fillMaxSize()) {
      Row() {
        
      }
    }
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier
          .size(70.dp)
          .padding(16.dp),
        color = Blue
      )
    }
  } else {
    if (ethernetStatus.value == "OK") {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(10.dp)
      ) {
        items(items = topFilmList) { film ->
          FilmCard(film)
        }
      }
    } else {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp), contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = ethernetStatus.value,
            fontSize = 20.sp,
          )
          MyButton {

          }
        }
      }
    }
  }
}

@Composable
fun MyButton(onClick: () -> Unit) {
  Button(
    onClick = { onClick() },
    colors = ButtonDefaults.buttonColors(Blue)
  ) {
    Text(text = "Reload", color = Color.White)
  }
}

@Composable
private fun FilmCard(film: FilmInfo) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
      .padding(5.dp)
      .clickable { },
    shape = RoundedCornerShape(10.dp),
    elevation = 5.dp
  ) {
    Row(modifier = Modifier.padding(10.dp)) {
      Image(
        painter = rememberImagePainter(data = film.posterUrl), modifier = Modifier.clip(
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

fun String.capitalizeFirstLetter(): String {
  val lowerCaseString = this.lowercase()
  return "${lowerCaseString.first().uppercaseChar()}${lowerCaseString.substring(1)}"
}

fun List<Genre>.toGenreList(): String {
  return joinToString { it.genre.capitalizeFirstLetter() }
}