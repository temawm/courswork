package com.example.reccomendation_app_courswork.navigation

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reccomendation_app_courswork.Screens.CatalogScreen
import com.example.reccomendation_app_courswork.Screens.HomeScreen

@Composable
fun NavControllerForHomeScreen(navHostController: NavHostController) {
    NavHost(navHostController, startDestination = "CatalogScreen") {
        composable("HomeScreen") {
            HomeScreen()
        }
        composable("CatalogScreen"){
            CatalogScreen()
        }
        composable("ProfileScreen") {
            ProfileScreen()
        }
    }
}
