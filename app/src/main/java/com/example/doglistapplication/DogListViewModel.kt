package com.example.doglistapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DogListViewModel : ViewModel() {
    val dogs = mutableStateListOf<Dog>()
    val favoriteDogs = mutableStateListOf<Dog>()
    val dogNames = mutableSetOf<String>()
    var searchText = mutableStateOf("")

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

    fun getFilteredDogs(): List<Dog> {
        return (favoriteDogs + dogs).filter {
            it.name.contains(searchText.value, ignoreCase = true)
        }
    }
}
