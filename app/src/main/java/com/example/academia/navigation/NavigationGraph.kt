package com.example.academia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academia.screen.FocusScreen
import com.example.academia.screen.HomeScreen
import com.example.academia.screen.ProfileScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen()
        }
        composable(Routes.FOCUS) {
            FocusScreen()
        }
        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}