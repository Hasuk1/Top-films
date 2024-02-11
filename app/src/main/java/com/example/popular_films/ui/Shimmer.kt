package com.example.popular_films.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.popular_films.ui.theme.Blue

@Composable
fun Shimmer() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp), contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .size(70.dp)
        .padding(16.dp), color = Blue
    )
  }
}