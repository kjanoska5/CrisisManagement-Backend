package com.example.crisismanagement.app.citizen.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    userId: String,
    onReportSent: () -> Unit,
    viewModel: ReportViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }
    var cameraGranted by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.setPhotoUri(imageUri.toString())
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        cameraGranted = results[Manifest.permission.CAMERA] == true
        locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationGranted) {
            getLocation(
                context = context,
                onSuccess = { lat, lng -> viewModel.setLocation(lat, lng) },
                onError = {
                    viewModel.setError(
                        "Failed to get location. Make sure location is turned on and try again."
                    )
                }
            )
        } else {
            viewModel.setError("Location permission is required to send a report.")
        }
    }

    LaunchedEffect(Unit) {
        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= 37) add(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }.toTypedArray()
        permissionsLauncher.launch(permissions)
    }

    LaunchedEffect(state.isSent) {
        if (state.isSent) {
            onReportSent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Report an incident", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        if (!locationGranted) {
            Text("The app requires location access to report an incident.")
            Spacer(Modifier.height(16.dp))
        }

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = state.selectedCategory?.name ?: "Select category",
                onValueChange = {},
                readOnly = true,
                label = { Text("Threat category") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                state.categoriesList.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            viewModel.selectCategory(type)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (state.lat != null && state.lng != null)
                "Location: ${"%.5f".format(state.lat)}, ${"%.5f".format(state.lng)}"
            else "Fetching location..."
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val uri = createImageUri(context)
            imageUri = uri
            cameraLauncher.launch(uri)
        }) {
            Text(if (state.photoUri != null) "Photo attached ✓ (change)" else "Take photo")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.comment,
            onValueChange = { viewModel.setComment(it) },
            label = { Text("Comment (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = { viewModel.sendReport(userId) },
            enabled = !state.isSending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isSending) "Sending..." else "Send report")
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLocation(
    context: Context,
    onSuccess: (Double, Double) -> Unit,
    onError: () -> Unit
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val request = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setDurationMillis(15_000)
        .setMaxUpdateAgeMillis(10_000)
        .build()

    client.getCurrentLocation(request, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                onError()
            }
        }
        .addOnFailureListener { onError() }
}

private fun createImageUri(context: Context): Uri {
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "report_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
