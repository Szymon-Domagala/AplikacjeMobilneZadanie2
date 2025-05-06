package com.example.doglistapplication.ui.screens.addDog

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.doglistapplication.DogListApplication
import com.example.doglistapplication.data.DogsPhotosRepository
import com.example.doglistapplication.model.DogPhoto
import kotlinx.coroutines.launch


class AddDogViewModel(private val dogsPhotosRepository: DogsPhotosRepository
) : ViewModel() {
    var name = mutableStateOf("")
    var breed = mutableStateOf("")

    sealed interface UiState {
        data class Success(val photo: DogPhoto): UiState
        object Error: UiState
        object Loading: UiState
    }

    var uiState: UiState by mutableStateOf(UiState.Loading); private set

    init {
        getDogImage()
    }

    fun getDogImage() {
        viewModelScope.launch {
            uiState = UiState.Loading
            uiState = try {
                val image = dogsPhotosRepository.getRandomImage()
                UiState.Success(image)
            } catch (e: Exception) {
                UiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DogListApplication)
                val dogsPhotosRepository = application.container.dogsPhotosRepository
                AddDogViewModel(dogsPhotosRepository)
            }
        }
    }

    fun clear() {
        name.value = ""
        breed.value = ""
    }
}