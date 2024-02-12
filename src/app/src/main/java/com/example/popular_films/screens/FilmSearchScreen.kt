package com.example.popular_films.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.popular_films.R
import com.example.popular_films.ui.theme.Alpha0
import com.example.popular_films.ui.theme.Blue
import com.example.popular_films.ui.theme.LightBlue

@Composable
fun FilmSearchScreen(navController: NavController) {
  val text = remember { mutableStateOf("") }
  Column(
    modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(15.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(painter = painterResource(id = com.example.popular_films.R.drawable.backarrow),
        contentDescription = "back_arrow",
        modifier = Modifier
          .size(35.dp)
          .clickable {
            navController.popBackStack("FilmSearchScreen", inclusive = true)
            navController.navigate("PopularFilmListScreen")
          })
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        TextField(
          colors = TextFieldDefaults.textFieldColors(
            textColor = Alpha0,
            backgroundColor = Alpha0,
            focusedIndicatorColor = Blue,
            unfocusedIndicatorColor = LightBlue
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true,
          maxLines = 1,
          value = text.value,
          onValueChange = { newText -> text.value = newText },
          modifier = Modifier.fillMaxSize()
        )
        Text(
          text = text.value,
          color = Blue,
          fontSize = 25.sp,
          modifier = Modifier.align(Alignment.Center)
        )
      }

      Image(painter = painterResource(id = R.drawable.find),
        contentDescription = "find",
        modifier = Modifier
          .size(35.dp)
          .clickable {

          })
    }
  }
}