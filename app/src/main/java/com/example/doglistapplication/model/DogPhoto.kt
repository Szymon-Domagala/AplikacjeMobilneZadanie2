package com.example.doglistapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class DogPhoto (
    val message: String,
    val status: String
)