package com.example.doglistapplication.data

import com.example.doglistapplication.data.network.DogsService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer{
    val dogsPhotosRepository: DogsPhotosRepository
}

class DefaultAppContainer : AppContainer{
    private val dogsApiBaseUrl = "https://dog.ceo/api/breeds/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(dogsApiBaseUrl)
        .build()

    private val dogsService: DogsService by lazy{
        retrofit.create(DogsService::class.java)
    }


    override val dogsPhotosRepository: DogsPhotosRepository by lazy {
        NetworkDogsPhotosRepository(dogsService)
    }
}

