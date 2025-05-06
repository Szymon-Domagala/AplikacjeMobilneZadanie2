package com.example.doglistapplication.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.doglistapplication.R
import com.example.doglistapplication.model.DogDetails
import com.example.doglistapplication.model.DogListScreen
import com.example.doglistapplication.ui.screens.dogList.DogListViewModel

@Composable
fun DetailsScreen(
    navController: NavController,
    route: DogDetails,
    dogListViewModel: DogListViewModel,
    detailsViewModel: DetailsViewModel = DetailsViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate(DogListScreen) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Detale", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            IconButton(onClick = {
                detailsViewModel.removeDogByName(route.name, dogListViewModel)
                navController.navigate(DogListScreen)
            }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(route.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Dog Image",
                placeholder = painterResource(id = R.drawable.dog_icon),
                error = painterResource(id = R.drawable.dog_icon)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(route.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(route.breed, fontSize = 14.sp, color = Color.Gray)
        }
    }
}