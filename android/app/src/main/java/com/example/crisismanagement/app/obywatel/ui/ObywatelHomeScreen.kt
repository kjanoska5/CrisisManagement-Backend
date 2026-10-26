package com.example.crisismanagement.app.obywatel.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.app.common.components.MapaWidok
import com.example.crisismanagement.app.common.components.MapaZagrozenViewModel

@Composable
fun ObywatelHomeScreen(
    onNewReportClick: () -> Unit,
    mapaZagrozenViewModel: MapaZagrozenViewModel = viewModel()
) {
    val mapState by mapaZagrozenViewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok(
            reports = mapState.reports,
            chosenReport = mapState.chosenReports,
            categories = mapState.categories,
            onReportClick = { mapaZagrozenViewModel.chooseReport(it) },
            onDismissDialog = { mapaZagrozenViewModel.chooseReport(null) },
            contentPadding = PaddingValues(bottom = 88.dp)
        )

        FloatingActionButton(
            onClick = onNewReportClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Zgłoś zdarzenie")
        }
        // TODO: przycisk SOS (issue 6.1)
    }
}