package com.example.popular_films.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoInternet(ethernetStatus: MutableState<String>) {
  Box(
    modifier = Modifier
      .fillMaxHeight(0.89F)
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxHeight(0.8F)
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = ethernetStatus.value,
        fontSize = 20.sp,
      )
    }
  }
}