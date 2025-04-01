package com.example.doglistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doglistapplication.ui.theme.DogListApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogListApplicationTheme {
                val dogs = remember { mutableStateListOf<Dog>() }
                val favoriteDogs = remember { mutableStateListOf<Dog>() }
                val dogNames = remember { mutableSetOf<String>() }

                fun addDog(name: String) {
                    if (!dogNames.contains(name)) {
                        val newDog = Dog(name, "Jack Russel", false)
                        dogs.add(newDog)
                        dogNames.add(name)
                    }
                }

                fun toggleFavorite(dog: Dog) {
                    dog.isFavorite = !dog.isFavorite
                    if (dog.isFavorite) {
                        if (dogs.remove(dog)) {
                            favoriteDogs.add(0, dog)
                        }
                    } else {
                        if (favoriteDogs.remove(dog)) {
                            dogs.add(dog)
                        }
                    }
                }

                DogListScreen(dogs, favoriteDogs, dogNames, ::addDog, ::toggleFavorite)
            }
        }
    }
}

@Composable
fun DogListScreen(
    dogs: MutableList<Dog>,
    favoriteDogs: MutableList<Dog>,
    dogNames: MutableSet<String>,
    addDog: (String) -> Unit,
    toggleFavorite: (Dog) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isDuplicate by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", Modifier.padding(8.dp))
            Text("Doggo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Filled.AccountCircle, contentDescription = "Account", Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { text ->
                        searchText = text
                        isDuplicate = dogs.any { it.name == text } || favoriteDogs.any { it.name == text }
                    },
                    isError = isDuplicate,
                    placeholder = { Text("Poszukaj lub dodaj pieska 🐕") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Search, contentDescription = "Search")
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (!isDuplicate) {
                            addDog(searchText)
                            searchText = ""
                            isDuplicate = false
                        }
                    },
                    enabled = searchText.isNotBlank() && !isDuplicate
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add")
                }
            }

            if (isDuplicate) {
                Text("Piesek o tej nazwie już istnieje", color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("🐶: ${dogs.size + favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("❤\uFE0F: ${favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(favoriteDogs + dogs) { dog ->
                    DogItem(
                        dog = dog,
                        onDelete = {
                            dogNames.remove(dog.name)
                            favoriteDogs.remove(dog)
                            dogs.remove(dog)
                        },
                        onFavorite = { toggleFavorite(dog) }
                    )
                }
            }
        }
    }
}

@Composable
fun DogItem(dog: Dog, onDelete: () -> Unit, onFavorite: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_icon),
                contentDescription = "Dog Image",
                modifier = Modifier.size(48.dp)
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

data class Dog(val name: String, val breed: String, var isFavorite: Boolean = false)





