package com.example.kitabu_madb372_sf1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kitabu_madb372_sf1.di.KitabuApplication
import com.example.kitabu_madb372_sf1.ui.navigation.AppNavigation
import com.example.kitabu_madb372_sf1.ui.theme.Kitabu_MADB372_SF1Theme
import com.example.kitabu_madb372_sf1.ui.viewmodel.CatalogViewModel
import com.example.kitabu_madb372_sf1.ui.viewmodel.DashboardViewModel
import com.example.kitabu_madb372_sf1.ui.viewmodel.kitabuViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val app = application as KitabuApplication

        val factory = kitabuViewModelFactory(
            repository = app.container.libraryRepository
        )

        setContent {
            Kitabu_MADB372_SF1Theme {

                val catalogViewModel: CatalogViewModel =
                    viewModel(factory = factory)

                val dashboardViewModel: DashboardViewModel =
                    viewModel(factory = factory)

                AppNavigation(
                    catalogViewModel = catalogViewModel,
                    dashboardViewModel = dashboardViewModel
                )
            }
        }
    }
}