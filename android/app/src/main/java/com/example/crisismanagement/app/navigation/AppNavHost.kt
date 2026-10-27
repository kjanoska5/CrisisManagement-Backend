package com.example.crisismanagement.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.app.login.LoginScreen

// TODO: remove when real auth is added - single place for mock ID
private const val TEMP_USER_ID = "a4bab6f2-60fa-461a-b7b5-746b0e883896"

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val logout: () -> Unit = {
        navController.navigate(Route.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Route.Login.route) {

        composable(Route.Login.route) {
            LoginScreen(
                onLoggedIn = { role ->
                    val target = when (role) {
                        Role.CITIZEN -> Route.CitizenHome.route
                        Role.SERVICES -> Route.ServicesHome.route
                    }
                    navController.navigate(target) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.CitizenHome.route) {
            MainContainer(
                role = Role.CITIZEN,
                userId = TEMP_USER_ID,
                onLogout = logout
            )
        }

        composable(Route.ServicesHome.route) {
            MainContainer(
                role = Role.SERVICES,
                userId = TEMP_USER_ID,
                onLogout = logout
            )
        }
    }
}
