package com.example.doglistapplication.model

import kotlinx.serialization.Serializable

@Serializable
object DogListScreen

@Serializable
object AddDogScreen

@Serializable
object SettingsScreen

@Serializable
object AccountScreen

@Serializable
data class DogDetails(val name: String, val breed: String, val imageUrl: String)

