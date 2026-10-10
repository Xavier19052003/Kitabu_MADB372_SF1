package com.example.kitabu_madb372_sf1.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitabu_madb372_sf1.data.repository.LibraryRepository

fun kitabuViewModelFactory(
    repository: LibraryRepository
): ViewModelProvider.Factory {

    return viewModelFactory {

        initializer {
            CatalogViewModel(
                repository = repository
            )
        }

        initializer {
            DashboardViewModel(
                repository = repository
            )
        }
    }
}