package com.example.reccomendation_app_courswork.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reccomendation_app_courswork.Screens.BookCardScreen
import com.example.reccomendation_app_courswork.Screens.CatalogScreen
import com.example.reccomendation_app_courswork.Screens.HomeScreen
import com.example.reccomendation_app_courswork.Screens.ProfileScreen
import com.example.reccomendation_app_courswork.googleBooks.BookItem
import com.example.reccomendation_app_courswork.googleBooks.BookResponse
import com.google.gson.Gson
import java.net.URLDecoder

@Composable
fun NavControllerForHomeScreen(navHostController: NavHostController) {
    NavHost(navHostController, startDestination = "CatalogScreen") {
        composable("HomeScreen") {
            HomeScreen()
        }
        composable("CatalogScreen"){
            CatalogScreen(navHostController)
        }
        composable("ProfileScreen") {
            ProfileScreen()
        }
        composable("BookCardScreen/{bookItem}") { backStackEntry ->
            val gson = Gson()
            val bookJson = URLDecoder.decode(backStackEntry.arguments?.getString("bookItem"), "UTF-8")
            val bookItem = gson.fromJson(bookJson, BookItem::class.java)
            bookItem?.let {
                BookCardScreen(bookItem = it)
            }
        }
    }
}
