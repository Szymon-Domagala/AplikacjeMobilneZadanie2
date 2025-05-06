package com.example.doglistapplication.data.network

import com.example.doglistapplication.model.DogPhoto
import retrofit2.http.GET

interface DogsService {
    @GET("image/random")
    suspend fun getRandomDogImage(): DogPhoto
}
