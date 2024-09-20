package com.example.reccomendation_app_courswork.Screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.reccomendation_app_courswork.R
import com.example.reccomendation_app_courswork.navigation.AppNavGraph
import com.example.reccomendation_app_courswork.navigation.NavControllerForHomeScreen

sealed class BottomHomeScreenNavigation(val title: String, val iconId: Int, val route: String) {
    object CatalogScreen :
        BottomHomeScreenNavigation("Каталог", R.drawable.catalogicon, "CatalogScreen")

    object ProfileScreen :
        BottomHomeScreenNavigation("Профиль", R.drawable.profileicon, "ProfileScreen")
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    val navControllerForHomeScreen = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation(navControllerForHomeScreen)
        }
    ) {
        NavControllerForHomeScreen(navControllerForHomeScreen)
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val listBottomNavigationItems = listOf(
        BottomHomeScreenNavigation.CatalogScreen,
        BottomHomeScreenNavigation.ProfileScreen
    )
    androidx.compose.material.BottomNavigation(
        backgroundColor = Color.White,
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        listBottomNavigationItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    Log.d("HomeScreen.kt", "navController started")
                    val route = item.route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = "Icon")
                },
                label = {
                    Text(
                        text = item.title,
                        color = Color.Gray,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = colorResource(id = R.color.authorizationMark),
                unselectedContentColor = Color.Gray
            )
        }

    }
}