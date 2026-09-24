package com.example.crisismanagement.app.navigation

sealed class Route(val route: String) {
    object Login : Route("login")
    object ObywatelHome : Route("obywatel/home")
    object SluzbyHome : Route("sluzby/home")
    object ZgloszenieForm : Route("obywatel/zgloszenie")
    object MedicalCard : Route("obywatel/medical-card")
}