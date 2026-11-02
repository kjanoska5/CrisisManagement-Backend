package com.example.crisismanagement.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisismanagement.app.common.auth.SessionManager
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.app.login.LoginScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var session by remember { mutableStateOf(sessionManager.load()) }
    val startRoute = remember { session?.role?.homeRoute() ?: Route.Login.route }

    val logout: () -> Unit = {
        sessionManager.clear()
        session = null
        navController.navigate(Route.Login.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = startRoute) {

        composable(Route.Login.route) {
            LoginScreen(
                onLoggedIn = { newSession ->
                    sessionManager.save(newSession)
                    session = newSession
                    navController.navigate(newSession.role.homeRoute()) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.CitizenHome.route) {
            session?.let { current ->
                MainContainer(session = current, onLogout = logout)
            }
        }

        composable(Route.ServicesHome.route) {
            session?.let { current ->
                MainContainer(session = current, onLogout = logout)
            }
        }
    }
}

private fun Role.homeRoute(): String = when (this) {
    Role.CITIZEN -> Route.CitizenHome.route
    Role.SERVICES -> Route.ServicesHome.route
}