package com.example.crisismanagement.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisismanagement.app.enums.Rola
import com.example.crisismanagement.app.login.LoginScreen
import com.example.crisismanagement.app.obywatel.ui.ObywatelHomeScreen
import com.example.crisismanagement.app.obywatel.zgloszenie.ZgloszenieScreen
import com.example.crisismanagement.app.sluzby.ui.SluzbyHomeScreen

/**
 * Główny graf nawigacji aplikacji.
 *
 * Realizuje kryterium akceptacji issue #5:
 * "Użytkownik loguje się i trafia do odpowiedniego modułu wg roli".
 */
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

        composable(Route.ZgloszenieForm.route) {
            ZgloszenieScreen(
                // TODO: docelowo prawdziwe ID zalogowanego użytkownika (patrz issue o autoryzacji)
                uzytkownikId = "f93862e6-9b5f-4ba9-9664-d6a306370c66",
                onZgloszeniaWyslano = { navController.popBackStack() }
            )
        }

        composable(Route.SluzbyHome.route) {
            SluzbyHomeScreen()
        }
    }
}