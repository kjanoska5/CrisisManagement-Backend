package com.example.crisismanagement.app.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.app.common.components.ConnectionStatus
import com.example.crisismanagement.app.common.network.ConnectionViewModel
import com.example.crisismanagement.app.enums.Rola
import java.nio.file.WatchEvent

/**
 * TYMCZASOWY ekran logowania (stub).
 *
 * Celem issue #5 jest struktura nawigacji wg roli, a nie pełna, bezpieczna
 * autoryzacja z backendem — tego jeszcze nie mamy. Prawdziwe logowanie
 * (formularz email/hasło + wywołanie API) warto rozbić na osobne issue,
 * gdy backend będzie gotowy do obsługi autoryzacji.
 */
@Composable
fun LoginScreen
            (onZalogowano: (Rola) -> Unit,
             connectionViewModel: ConnectionViewModel = viewModel()
) {
    val connectionState by connectionViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConnectionStatus(
            state = connectionState,
            onRetry = { connectionViewModel.checkConnection()}
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Zaloguj się (wersja testowa)")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { onZalogowano(Rola.OBYWATEL) }) {
            Text("Zaloguj jako Obywatel")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onZalogowano(Rola.SLUZBY) }) {
            Text("Zaloguj jako Służby")
        }
    }
}