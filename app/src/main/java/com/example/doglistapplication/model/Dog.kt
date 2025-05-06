package com.example.doglistapplication.model

data class Dog (
    val name: String,
    val breed: String,
    val imageString: String,
    var isFavorite: Boolean = false
)