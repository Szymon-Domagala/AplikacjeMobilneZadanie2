package com.example.doglistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.doglistapplication.model.AccountScreen
import com.example.doglistapplication.model.AddDogScreen
import com.example.doglistapplication.model.DogDetails
import com.example.doglistapplication.model.DogListScreen
import com.example.doglistapplication.model.SettingsScreen
import com.example.doglistapplication.ui.screens.account.AccountScreen
import com.example.doglistapplication.ui.screens.addDog.AddDogScreen
import com.example.doglistapplication.ui.screens.addDog.AddDogViewModel
import com.example.doglistapplication.ui.screens.details.DetailsScreen
import com.example.doglistapplication.ui.screens.details.DetailsViewModel
import com.example.doglistapplication.ui.screens.dogList.DogListScreen
import com.example.doglistapplication.ui.screens.dogList.DogListViewModel
import com.example.doglistapplication.ui.screens.settings.SettingsScreen
import com.example.doglistapplication.ui.theme.DogListApplicationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogListApplicationTheme {
                val navController = rememberNavController()
                val dogListViewModel: DogListViewModel = viewModel()
                val detailsViewModel: DetailsViewModel = viewModel()


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = DogListScreen,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<DogListScreen> {
                            DogListScreen(
                                navController = navController,
                                viewModel = dogListViewModel
                            )
                        }

                        composable<AddDogScreen> {
                            val viewModel: AddDogViewModel =
                                viewModel(factory = AddDogViewModel.Factory)
                            AddDogScreen(
                                navController = navController,
                                viewModel = viewModel,
                                viewModel.uiState,
                                viewModel::getDogImage,
                                onAdd = { name, breed, imageUrl ->
                                    dogListViewModel.addDog(name, breed, imageUrl)
                                }
                            )
                        }

                        composable<SettingsScreen> { SettingsScreen(navController) }

                        composable<AccountScreen> { AccountScreen(navController) }

                        composable<DogDetails> { backStackEntry ->
                            val args = backStackEntry.toRoute<DogDetails>()
                            DetailsScreen(
                                navController = navController,
                                route = args,
                                dogListViewModel = dogListViewModel,
                                detailsViewModel = detailsViewModel
                            )
                        }

                    }
                }
            }
        }
    }
}







