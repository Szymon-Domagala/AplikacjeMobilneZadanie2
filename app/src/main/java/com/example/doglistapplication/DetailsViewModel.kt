package com.example.doglistapplication

import androidx.lifecycle.ViewModel

class DetailsViewModel : ViewModel() {
    fun removeDogByName(name: String, dogs: MutableList<Dog>): Dog? {
        return dogs.find { it.name == name }?.also { dogs.remove(it) }
    }
}
