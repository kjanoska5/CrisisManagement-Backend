package com.example.crisismanagement.app.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
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

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    reports: List<ReportSummary> = emptyList(),
    chosenReport: ReportSummary? = null,
    categories: Map<String, String> = emptyMap(),
    onReportClick: (ReportSummary) -> Unit = {},
    onDismissDialog: () -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    draftPosition: LatLng? = null,
) {
    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        contentPadding = contentPadding,
        onMapClick = onMapClick
    ) {
        draftPosition?.let { position ->
            Marker(
                state = rememberMarkerState(
                    key = "draft-${position.latitude}-${position.longitude}",
                    position = position
                ),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
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
    if (chosenReport != null) {
        val categoryName = categories[chosenReport.threatTypeId] ?: chosenReport.threatTypeId
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Report details") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Status: ${chosenReport.status ?: "ACTIVE"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Threat type: $categoryName", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Description: ${chosenReport.comment ?: "No description provided"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Location: ${chosenReport.lat}, ${chosenReport.lng}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Close")
                }
            }
        )
    }
}
