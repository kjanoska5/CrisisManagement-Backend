package com.example.crisismanagement.app.navigation

sealed class Route(val route: String) {
    object Login : Route("login")
    object CitizenHome : Route("citizen/home")
    object ServicesHome : Route("services/home")
    object ReportForm : Route("citizen/report")
}
