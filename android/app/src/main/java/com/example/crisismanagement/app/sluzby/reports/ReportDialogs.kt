package com.example.crisismanagement.app.sluzby.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.components.ThreatTypeDropdown
import com.example.crisismanagement.app.common.network.ReportSummary

@Composable
fun AddReportDialog(
    categories: Map<String, String>,
    isSaving: Boolean,
    onConfirm: (threatTypeId: String, comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTypeId by remember { mutableStateOf<String?>(null) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.service_add_report_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ThreatTypeDropdown(
                    categories = categories,
                    selectedId = selectedTypeId,
                    onSelected = { selectedTypeId = it }
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(stringResource(R.string.comment_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = selectedTypeId != null && !isSaving,
                onClick = { selectedTypeId?.let { onConfirm(it, comment) } }
            ) {
                Text(stringResource(if (isSaving) R.string.common_saving else R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

@Composable
fun ManageReportDialog(
    report: ReportSummary,
    categories: Map<String, String>,
    isSaving: Boolean,
    onSave: (threatTypeId: String, comment: String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTypeId by remember(report.id) { mutableStateOf<String?>(report.threatTypeId) }
    var comment by remember(report.id) { mutableStateOf(report.comment ?: "") }
    var showDeleteConfirmation by remember(report.id) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.service_manage_report_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${stringResource(R.string.status_label)}: ${report.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
                ThreatTypeDropdown(
                    categories = categories,
                    selectedId = selectedTypeId,
                    onSelected = { selectedTypeId = it }
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(stringResource(R.string.comment_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = selectedTypeId != null && !isSaving,
                onClick = { selectedTypeId?.let { onSave(it, comment) } }
            ) {
                Text(stringResource(if (isSaving) R.string.common_saving else R.string.common_save))
            }
        },
        dismissButton = {
            Column {
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    enabled = !isSaving,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.common_delete))
                }
                TextButton(onClick = onDismiss, enabled = !isSaving) {
                    Text(stringResource(R.string.common_close))
                }
            }
        }
    )

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDelete()
                }) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}