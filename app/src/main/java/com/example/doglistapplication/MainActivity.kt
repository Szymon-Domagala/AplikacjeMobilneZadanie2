package com.example.doglistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doglistapplication.ui.theme.DogListApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogListApplicationTheme {
                val navController = rememberNavController()

                val dogs = remember { mutableStateListOf<Dog>() }
                val favoriteDogs = remember { mutableStateListOf<Dog>() }
                val dogNames = remember { mutableSetOf<String>() }

                fun addDog(name: String, breed: String) {
                    if (!dogNames.contains(name)) {
                        val newDog = Dog(name, breed, false)
                        dogs.add(newDog)
                        dogNames.add(name)
                    }
                }

                fun removeDog(dog: Dog) {
                    dogNames.remove(dog.name)
                    favoriteDogs.remove(dog)
                    dogs.remove(dog)
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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dog_list_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dog_list_screen") {
                            DogListScreen(
                                navController, dogs, favoriteDogs, dogNames,
                                toggleFavorite = ::toggleFavorite
                            )
                        }
                        composable("add_dog_screen") {
                            AddDogScreen(navController) { name, breed ->
                                addDog(name, breed)
                            }
                        }
                        composable("settings_screen") { SettingsScreen(navController) }
                        composable("AccountScreen") { AccountScreen(navController) }
                        composable("DetailsScreen/{dogName}/{dogBreed}") { backStackEntry ->
                            val dogName = backStackEntry.arguments?.getString("dogName") ?: "Brak danych"
                            val dogBreed = backStackEntry.arguments?.getString("dogBreed") ?: "Brak danych"
                            DetailsScreen(navController, dogName, dogBreed) { name ->
                                val dogToRemove = dogs.find { it.name == name }
                                dogToRemove?.let { removeDog(it) }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun DogListScreen(
    navController: NavController,
    dogs: MutableList<Dog>,
    favoriteDogs: MutableList<Dog>,
    dogNames: MutableSet<String>,
    toggleFavorite: (Dog) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("settings_screen") }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
            Text("Doggo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { navController.navigate("AccountScreen") }) {
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
                    value = searchText,
                    onValueChange = { text -> searchText = text },
                    placeholder = { Text("Poszukaj pieska 🐕") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { navController.navigate("add_dog_screen") }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("🐶: ${dogs.size + favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("❤\uFE0F: ${favoriteDogs.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            val filteredDogs = (favoriteDogs + dogs).filter {
                it.name.contains(searchText, ignoreCase = true)
            }

            LazyColumn {
                items(filteredDogs) { dog ->
                    DogItem(
                        dog = dog,
                        navController = navController,
                        onDelete = {
                            dogNames.remove(dog.name)
                            favoriteDogs.remove(dog)
                            dogs.remove(dog)
                        },
                        onFavorite = {
                            toggleFavorite(dog)
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
                .clickable { navController.navigate("DetailsScreen/${dog.name}/${dog.breed}")
                },
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

@Composable
fun AddDogScreen(navController: NavController, addDog: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Dodaj Psa",
                modifier = Modifier.weight(1f),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_icon),
                contentDescription = "Dog Image",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Imie") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                placeholder = { Text("Rasa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && breed.isNotBlank()) {
                        addDog(name, breed)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCE93D8))
            ) {
                Text("Add")
            }
        }
    }
}


@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("dog_list_screen") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Ustawienia", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AccountScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("dog_list_screen") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Profil", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Large Account Icon", modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Jan Brzechwa", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController, dogName: String, dogBreed: String, removeDog: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("dog_list_screen") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Detale", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            IconButton(onClick = {
                removeDog(dogName)
                navController.navigate("dog_list_screen")
            }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_icon),
                contentDescription = "Dog Image",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(dogName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(dogBreed, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

data class Dog(val name: String, val breed: String, var isFavorite: Boolean = false)

@Preview(showBackground = true)
@Composable
fun PreviewDogListScreen() {
    DogListApplicationTheme {
        val navController = rememberNavController()
        val dogs = remember { mutableStateListOf<Dog>() }
        val favoriteDogs = remember { mutableStateListOf<Dog>() }
        val dogNames = remember { mutableSetOf<String>() }

        DogListScreen(
            navController = navController,
            dogs = dogs,
            favoriteDogs = favoriteDogs,
            dogNames = dogNames,
            toggleFavorite = {}
        )
    }
}