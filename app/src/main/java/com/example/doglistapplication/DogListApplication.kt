package com.example.doglistapplication

import android.app.Application
import com.example.doglistapplication.data.AppContainer
import com.example.doglistapplication.data.DefaultAppContainer

class DogListApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate(){
        super.onCreate()
        container = DefaultAppContainer()
    }
}