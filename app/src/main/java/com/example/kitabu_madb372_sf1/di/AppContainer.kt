package com.example.kitabu_madb372_sf1.di

import android.content.Context
import com.example.kitabu_madb372_sf1.data.local.AppDatabase
import com.example.kitabu_madb372_sf1.data.repository.LibraryRepository

class AppContainer(context: Context) {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context.applicationContext)
    }

    val libraryRepository: LibraryRepository by lazy {
        LibraryRepository(
            bookDao = database.bookDao(),
            bookingDao = database.bookingDao()
        )
    }
}