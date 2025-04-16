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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doglistapplication.ui.theme.DogListApplicationTheme
import com.verticalcoding.mystudentlist.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogListApplicationTheme {
                val navController = rememberNavController()
                val dogListViewModel: DogListViewModel = viewModel()
                val addDogViewModel: AddDogViewModel = viewModel()
                val detailsViewModel: DetailsViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dog_list_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dog_list_screen") {
                            DogListScreen(
                                navController = navController,
                                viewModel = dogListViewModel
                            )
                        }

                        composable("add_dog_screen") {
                            AddDogScreen(
                                navController = navController,
                                viewModel = addDogViewModel,
                                onAdd = { name, breed ->
                                    dogListViewModel.addDog(name, breed)
                                }
                            )
                        }

                        composable("settings_screen") { SettingsScreen(navController) }

                        composable("AccountScreen") { AccountScreen(navController) }

                        composable("DetailsScreen/{dogName}/{dogBreed}") { backStackEntry ->
                            val dogName = backStackEntry.arguments?.getString("dogName") ?: ""
                            val dogBreed = backStackEntry.arguments?.getString("dogBreed") ?: ""
                            DetailsScreen(
                                navController = navController,
                                dogName = dogName,
                                dogBreed = dogBreed,
                                onRemove = {
                                    val dog = detailsViewModel.removeDogByName(it, dogListViewModel.dogs)
                                    dog?.let { removed -> dogListViewModel.removeDog(removed) }
                                }
                            )
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
                    value = searchText.value,
                    onValueChange = { viewModel.searchText.value = it },
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
fun AddDogScreen(
    navController: NavController,
    viewModel: AddDogViewModel,
    onAdd: (String, String) -> Unit
) {
    val name = viewModel.name
    val breed = viewModel.breed

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
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = { Text("Imie") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = breed.value,
                onValueChange = { breed.value = it },
                placeholder = { Text("Rasa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.value.isNotBlank() && breed.value.isNotBlank()) {
                        onAdd(name.value, breed.value)
                        viewModel.clear()
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
fun DetailsScreen(
    navController: NavController,
    dogName: String,
    dogBreed: String,
    onRemove: (String) -> Unit
) {
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
                onRemove(dogName)
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

data class Dog(
    val name: String,
    val breed: String,
    var isFavorite: Boolean = false
)

