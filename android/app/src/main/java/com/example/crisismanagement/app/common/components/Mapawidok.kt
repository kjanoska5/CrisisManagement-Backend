package com.example.crisismanagement.app.common.components
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Współdzielony komponent mapy (Google Maps SDK, zgodnie z ADR-0001).
 *
 * Realizuje kryterium akceptacji issue #5:
 * "Wspólne komponenty (np. mapa) są re-używalne między modułami".
 *
 * Używany zarówno w module obywatela (mapa zagrożeń, issue 4.1),
 * jak i w module służb (oznaczanie zdarzeń, issue 3.1) — bez
 * duplikowania kodu inicjalizacji mapy w obu miejscach.
 */
@Composable
fun MapaWidok(modifier: Modifier = Modifier) {
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
    // TODO: pinezki zdarzeń dojdą w issue 4.1, oznaczanie w issue 3.1
}