package com.example.crisismanagement.app.citizen.medicalcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.components.AppTextField
import com.example.crisismanagement.app.common.components.InfoCard
import com.example.crisismanagement.app.common.components.PrimaryButton
import com.example.crisismanagement.app.common.components.SecondaryButton

@Composable
fun MedicalCardScreen(
    userId: String,
    onSaved: () -> Unit,
    viewModel: MedicalCardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.downloadCard(userId)
    }

    LaunchedEffect(state.saved, state.deleted) {
        if (state.saved || state.deleted) {
            viewModel.consumeResult()
            onSaved()
        }
    }

    if (state.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AppTextField(
            label = "Blood type",
            value = state.bloodType,
            onValueChange = { viewModel.setBloodType(it) },
            placeholder = "Not provided"
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            label = "Allergies",
            value = state.allergies,
            onValueChange = { viewModel.setAllergies(it) },
            placeholder = "Not provided"
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            label = "Medications",
            value = state.medicines,
            onValueChange = { viewModel.setMedicines(it) },
            placeholder = "Not provided"
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            label = "Emergency contact",
            value = state.emergencyContact,
            onValueChange = { viewModel.setEmergencyContact(it) },
            placeholder = "Not provided"
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            label = "Chronic diseases",
            value = state.chronicDiseases,
            onValueChange = { viewModel.setChronicDiseases(it) },
            placeholder = "Not provided"
        )
        Spacer(Modifier.height(16.dp))

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        PrimaryButton(
            text = if (state.saving) stringResource(R.string.common_saving) else if (state.cardExists) "Save card" else "Fill card",
            onClick = { viewModel.saveCard(userId) },
            enabled = !state.saving && !state.deleting
        )
        Spacer(Modifier.height(12.dp))
        InfoCard("Provide only necessary information. You decide what to share with emergency services.")

        if (state.cardExists) {
            Spacer(Modifier.height(16.dp))
            SecondaryButton(
                text = if (state.deleting) "Deleting..." else "Delete card",
                onClick = { showDeleteDialog = true },
                enabled = !state.saving && !state.deleting,
                isDanger = true
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete medical card?") },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteCard(userId)
                }) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}
