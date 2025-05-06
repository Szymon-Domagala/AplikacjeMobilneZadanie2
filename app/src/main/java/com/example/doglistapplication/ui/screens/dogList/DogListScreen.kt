package com.example.doglistapplication.ui.screens.dogList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doglistapplication.R
import com.example.doglistapplication.model.AccountScreen
import com.example.doglistapplication.model.AddDogScreen
import com.example.doglistapplication.model.Dog
import com.example.doglistapplication.model.DogDetails
import com.example.doglistapplication.model.SettingsScreen

@Composable
fun DogListScreen(
    navController: NavController,
    viewModel: DogListViewModel
) {
    val searchText = viewModel.searchText
    val dogs = viewModel.dogs
    val favoriteDogs = viewModel.favoriteDogs

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate(SettingsScreen) }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
            Text("Doggo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { navController.navigate(AccountScreen) }) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Account")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { viewModel.searchText.value = it },
                    placeholder = { Text("Poszukaj pieska 🐕") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { navController.navigate(AddDogScreen) }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("🐶: ${dogs.size + favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.width(6.dp))

                Icon(Icons.Filled.Favorite, contentDescription = "Favorite Dogs", tint = Color.Red)
                Spacer(modifier = Modifier.width(4.dp))
                Text("${favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            val filteredDogs = viewModel.getFilteredDogs()

            LazyColumn {
                items(filteredDogs) { dog ->
                    DogItem(
                        dog = dog,
                        navController = navController,
                        onDelete = {
                            viewModel.removeDog(dog)
                        },
                        onFavorite = {
                            viewModel.toggleFavorite(dog)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DogItem(dog: Dog, navController: NavController, onDelete: () -> Unit, onFavorite: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { navController.navigate(DogDetails(dog.name,dog.breed, dog.imageString))
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dog.imageString,
                contentDescription = "Dog Image",
                modifier = Modifier.size(48.dp),
                placeholder = painterResource(id = R.drawable.dog_icon),
                error = painterResource(id = R.drawable.dog_icon)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = dog.name, fontWeight = FontWeight.Bold)
                Text(text = dog.breed, fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(onClick = { onFavorite() }) {
                Icon(
                    if (dog.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (dog.isFavorite) Color.Red else Color.Black
                )
            }

            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}