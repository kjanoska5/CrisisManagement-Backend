package com.example.crisismanagement.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisismanagement.app.enums.Rola
import com.example.crisismanagement.app.login.LoginScreen

// TODO: usunąć, gdy powstanie prawdziwa autoryzacja — na razie jedno miejsce do podmiany ID
private const val TYMCZASOWY_UZYTKOWNIK_ID = "a4bab6f2-60fa-461a-b7b5-746b0e883896"

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
                onZalogowano = { rola ->
                    val cel = when (rola) {
                        Rola.OBYWATEL -> Route.ObywatelHome.route
                        Rola.SLUZBY -> Route.SluzbyHome.route
                    }
                    navController.navigate(cel) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.ObywatelHome.route) {
            MainContainer(
                role = Rola.OBYWATEL,
                userId = TYMCZASOWY_UZYTKOWNIK_ID,
                onLogout = logout
            )
        }

        composable(Route.SluzbyHome.route) {
            MainContainer(
                role = Rola.SLUZBY,
                userId = TYMCZASOWY_UZYTKOWNIK_ID,
                onLogout = logout
            )
        }
    }
}