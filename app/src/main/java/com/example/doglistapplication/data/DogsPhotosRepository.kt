package com.example.doglistapplication.data

import com.example.doglistapplication.data.network.DogsService
import com.example.doglistapplication.model.DogPhoto

interface DogsPhotosRepository{
    suspend fun getRandomImage(): DogPhoto
}

class NetworkDogsPhotosRepository(
    private val dogsService: DogsService
): DogsPhotosRepository{
    override suspend fun getRandomImage(): DogPhoto = dogsService.getRandomDogImage()
    }


