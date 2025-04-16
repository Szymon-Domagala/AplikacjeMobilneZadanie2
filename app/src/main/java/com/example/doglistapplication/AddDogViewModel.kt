package com.example.doglistapplication

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf


class AddDogViewModel : ViewModel() {
    var name = mutableStateOf("")
    var breed = mutableStateOf("")



    fun clear() {
        name.value = ""
        breed.value = ""
    }
}