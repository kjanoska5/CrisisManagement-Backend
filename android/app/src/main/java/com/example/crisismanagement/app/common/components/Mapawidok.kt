package com.example.crisismanagement.app.common.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crisismanagement.app.common.network.ReportStatus
import com.example.crisismanagement.app.common.network.ReportSummary
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.key

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
fun MapaWidok(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    reports: List<ReportSummary> = emptyList(),
    chosenReport: ReportSummary? = null,
    categories: Map<String, String> = emptyMap(),
    onReportClick: (ReportSummary) -> Unit = {},
    onDismissDialog: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        contentPadding = contentPadding
    ) {
        reports.forEach { report ->
            key(report.id) {
                val markerIcon = when (report.status) {
                    ReportStatus.VERIFYING -> BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_YELLOW
                    )

                    ReportStatus.RESOLVED -> BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )

                    else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                }
                Marker(
                    state = rememberMarkerState(
                        key = report.id,
                        position = LatLng(report.lat, report.lng)
                    ),
                    icon = markerIcon,
                    onClick = {
                        onReportClick(report)
                        true
                    }
                )
            }
        }
    }
    if (chosenReport != null){
        val categoryName = categories[chosenReport.threatTypeId] ?: chosenReport.threatTypeId
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Szczegóły zgłoszenia") },
            text = {
                Column(modifier = Modifier.padding(8.dp)){
                    Text("Status: ${chosenReport.status ?: "AKTYWNE"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Typ zagrożenia: $categoryName", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Opis: ${chosenReport.comment?: "Brak dodatkowego opisu"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Lokalizacja: ${chosenReport.lat}, ${chosenReport.lng}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Zamknij")
                }
            }
        )
    }
}