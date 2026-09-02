package com.example.crisismanagement.app.obywatel.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.crisismanagement.app.common.components.MapaWidok

/**
 * Ekran startowy modułu obywatela.
 * Na razie tylko szkielet z mapą — konkretne funkcje (zgłaszanie, SOS itd.)
 * dochodzą w kolejnych issues (1.1, 6.1, ...).
 */
@Composable
fun ObywatelHomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok()
        // TODO: przycisk SOS (issue 6.1), przycisk zgłoszenia (issue 1.1)
    }
}
