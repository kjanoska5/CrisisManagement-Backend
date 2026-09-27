package com.example.crisismanagement.app.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisismanagement.app.enums.Rola
import com.example.crisismanagement.app.login.LoginScreen
import com.example.crisismanagement.app.navigation.Route
import com.example.crisismanagement.app.obywatel.medicalcard.MedicalCardScreen
import com.example.crisismanagement.app.obywatel.ui.ObywatelHomeScreen
import com.example.crisismanagement.app.obywatel.zgloszenie.ZgloszenieScreen
import com.example.crisismanagement.app.sluzby.ui.SluzbyHomeScreen

// TODO: usunąć, gdy powstanie prawdziwa autoryzacja — na razie jedno miejsce do podmiany ID
// PODMIEŃ na ID zwrócone przez: SELECT id, email FROM users;
private const val TYMCZASOWY_UZYTKOWNIK_ID = "a4bab6f2-60fa-461a-b7b5-746b0e883896"

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
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
            ObywatelHomeScreen(navController)
        }

        composable(Route.SluzbyHome.route) {
            SluzbyHomeScreen()
        }

        composable(Route.ZgloszenieForm.route) {
            ZgloszenieScreen(
                uzytkownikId = TYMCZASOWY_UZYTKOWNIK_ID,
                onZgloszeniaWyslano = { navController.popBackStack() }
            )
        }

        composable(Route.MedicalCard.route) {
            MedicalCardScreen(
                userId = TYMCZASOWY_UZYTKOWNIK_ID,
                onSaved = { navController.popBackStack() }
            )
        }
    }
}