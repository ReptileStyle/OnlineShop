package com.example.onlineshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.onlineshop.ui.home.HomeScreen
import com.example.onlineshop.ui.login.LoginScreen
import com.example.onlineshop.ui.profile.ProfileScreen
import com.example.onlineshop.ui.profile.ProfileScreenUi
import com.example.onlineshop.ui.registration.RegistrationScreen
import com.example.onlineshop.ui.splash.SplashScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.splash
    ) {
        composable( route = Route.signIn){
            RegistrationScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigate = navController::navigate
            )
        }
        composable( route = Route.login){
            LoginScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigate = navController::navigate
            )
        }
        composable( route = Route.profile){
            ProfileScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigate = navController::navigate,
                navController = navController
            )
        }
        composable( route = Route.home){
            HomeScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigate = navController::navigate,
                navController = navController
            )
        }
        composable( route = Route.splash){
            SplashScreen(
                onNavigateUp = { navController.navigateUp() },
                onNavigate = navController::navigate
            )
        }
    }
}