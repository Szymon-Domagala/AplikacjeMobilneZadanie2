package com.example.doglistapplication.ui.screens.details

import androidx.lifecycle.ViewModel
import com.example.doglistapplication.ui.screens.dogList.DogListViewModel

class DetailsViewModel : ViewModel() {

    fun removeDogByName(name: String, viewModel: DogListViewModel) {
        val dog = viewModel.dogs.find { it.name == name }
            ?: viewModel.favoriteDogs.find { it.name == name }

        dog?.let {
            viewModel.removeDog(it)
        }
    }
}

