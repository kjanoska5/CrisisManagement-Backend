package com.example.crisismanagement.app.sluzby.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.crisismanagement.app.common.components.MapaWidok

/**
 * Ekran startowy modułu służb.
 * Współdzieli MapaWidok() z modułem obywatela — dokładnie ten sam
 * komponent, inne funkcje dobudowane wokół niego (issue 3.1).
 */
@Composable
fun SluzbyHomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok()
        // TODO: oznaczanie zdarzeń (issue 3.1), wysyłka alertów (issue 5.1)
    }
}