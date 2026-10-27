package com.example.crisismanagement.app.citizen.ui

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
import com.example.crisismanagement.app.common.components.MapView
import com.example.crisismanagement.app.common.components.ThreatMapViewModel

@Composable
fun CitizenHomeScreen(
    onNewReportClick: () -> Unit,
    threatMapViewModel: ThreatMapViewModel = viewModel()
) {
    val mapState by threatMapViewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        MapView(
            reports = mapState.reports,
            chosenReport = mapState.chosenReport,
            categories = mapState.categories,
            onReportClick = { threatMapViewModel.chooseReport(it) },
            onDismissDialog = { threatMapViewModel.chooseReport(null) },
            contentPadding = PaddingValues(bottom = 88.dp)
        )

        FloatingActionButton(
            onClick = onNewReportClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Report incident")
        }
        // TODO: SOS button (issue 6.1)
    }
}
