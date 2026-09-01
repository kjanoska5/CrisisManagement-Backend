package com.example.crisismanagement.app.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crisismanagement.app.enums.Rola

/**
 * TYMCZASOWY ekran logowania (stub).
 *
 * Celem issue #5 jest struktura nawigacji wg roli, a nie pełna, bezpieczna
 * autoryzacja z backendem — tego jeszcze nie mamy. Prawdziwe logowanie
 * (formularz email/hasło + wywołanie API) warto rozbić na osobne issue,
 * gdy backend będzie gotowy do obsługi autoryzacji.
 */
@Composable
fun LoginScreen(onZalogowano: (Rola) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Zaloguj się (wersja testowa)")
        Button(onClick = { onZalogowano(Rola.OBYWATEL) }) {
            Text("Zaloguj jako Obywatel")
        }
        Button(onClick = { onZalogowano(Rola.SLUZBY) }) {
            Text("Zaloguj jako Służby")
        }
    }
}