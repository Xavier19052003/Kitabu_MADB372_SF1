package com.example.kitabu_madb372_sf1.di

import android.app.Application

class KitabuApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // Set up the app container.
        container = AppContainer(this)
    }
}