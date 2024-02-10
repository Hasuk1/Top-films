package com.example.popular_films

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.popular_films.ui.theme.PopularFilmsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PopularFilmsTheme {
        FilmList(this@MainActivity)
      }
    }
  }
}

fun isNetworkAvailable(context: Context): Boolean {
  val connectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
  return activeNetworkInfo?.isConnectedOrConnecting == true
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun FilmList(context: Context) {
  val topFilmList by remember {
    mutableStateOf(mutableStateListOf<FilmCardInfo>())
  }
  var ethernetStatus by remember { mutableStateOf("OK") }
  var isLoading by remember { mutableStateOf(true) }
  if (isNetworkAvailable(context)) {
    val kinopoiskApi = Retrofit.Builder().baseUrl("https://kinopoiskapiunofficial.tech")
      .addConverterFactory(GsonConverterFactory.create()).build().create(KinopoiskApi::class.java)

    LaunchedEffect(true) {
      try {
        val topFilmsResponse = withContext(Dispatchers.IO) {
          kinopoiskApi.getTopFilms()
        }
        topFilmsResponse.films.let {
          topFilmList.clear()
          topFilmList.addAll(it)
        }
        isLoading = false
      } catch (e: Exception) {
        ethernetStatus = "Ошибка загрузки..."
        Log.d("MyLog", "ApiLog = $e")
        isLoading = false
      }
    }
  } else {
    ethernetStatus = "Нет доступа в интернет"
  }

  if (isLoading) {
    CircularProgressIndicator(
      modifier = Modifier
        .size(50.dp)
        .padding(16.dp)
    )
  } else {


    if (ethernetStatus == "OK") {
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
        Text(
          text = ethernetStatus,
          fontSize = 20.sp,
        )
      }
    }
  }
}

@Composable
private fun FilmCard(film: FilmCardInfo) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
      .padding(5.dp),
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
          text = film.year.toString()
        )
      }
    }
  }

}