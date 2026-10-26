package com.example.crisismanagement.app.sluzby.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.components.MapaWidok

@Composable
fun ServiceMapScreen(
    userId: String,
    viewModel: ServiceMapViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok(
            reports = state.reports,
            categories = state.categories,
            onReportClick = viewModel::onReportSelected,
            onMapClick = viewModel::onMapTapped,
            draftPosition = state.draftPosition
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Text(
                text = stringResource(R.string.service_map_hint),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (state.draftPosition != null) {
        AddReportDialog(
            categories = state.categories,
            isSaving = state.isSaving,
            onConfirm = { typeId, comment -> viewModel.createReport(userId, typeId, comment) },
            onDismiss = viewModel::dismissDraft
        )
    }

    state.selectedReport?.let { report ->
        ManageReportDialog(
            report = report,
            categories = state.categories,
            isSaving = state.isSaving,
            onSave = viewModel::updateReport,
            onDelete = viewModel::deleteReport,
            onDismiss = viewModel::dismissSelected
        )
    }
}