package com.example.reccomendation_app_courswork.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reccomendation_app_courswork.Screens.HomeScreen
import com.example.reccomendation_app_courswork.Screens.LoginScreen
import com.example.reccomendation_app_courswork.Screens.LoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "LoginScreen") {
        composable("LoginScreen") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable( "HomeScreen"){
            HomeScreen()
        }
    }

}