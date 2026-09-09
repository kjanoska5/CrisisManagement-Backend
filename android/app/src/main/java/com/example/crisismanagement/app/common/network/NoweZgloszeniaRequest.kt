package com.example.crisismanagement.app.common.network

data class NoweZgloszenieRequest(
    val uzytkownikId: String,
    val typZagrozeniaId: String,
    val lat: Double,
    val lng: Double,
    val zdjecieUrl: String?,
    val komentarz: String?
)