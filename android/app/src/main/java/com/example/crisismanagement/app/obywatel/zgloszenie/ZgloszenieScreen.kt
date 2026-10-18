package com.example.crisismanagement.app.obywatel.zgloszenie

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
fun ZgloszenieScreen(
    uzytkownikId: String,
    onZgloszeniaWyslano: () -> Unit,
    viewModel: ZgloszenieViewModel = viewModel()
){
    val stan by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var zdjecieUri by remember { mutableStateOf<Uri?>(null) }
    var rozwinietyDropdown by remember { mutableStateOf(false) }
    var przyznanaLokalizacja by remember { mutableStateOf(false) }
    var przyznanyAparat by remember { mutableStateOf(false) }
    val launcherAparatu = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sukces ->
        if (sukces && zdjecieUri != null) {
            viewModel.ustawZdjecie(zdjecieUri.toString())
        }
    }
    val launcherUprawnien = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { wyniki ->
        przyznanyAparat = wyniki[Manifest.permission.CAMERA] == true
        przyznanaLokalizacja =
            wyniki[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    wyniki[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (przyznanaLokalizacja) {
            pobierzLokalizacje(
                context = context,
                onSukces = { lat, lng -> viewModel.ustawLokalizacje(lat, lng) },
                onBlad = {
                    viewModel.ustawBlad(
                        "Nie udało się pobrać lokalizacji. Sprawdź, czy lokalizacja jest włączona, i spróbuj ponownie."
                    )
                }
            )
        } else {
            viewModel.ustawBlad("Do zgłoszenia potrzebna jest zgoda na lokalizację.")
        }
    }

    LaunchedEffect(Unit) {
        val uprawnienia = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= 37) add(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }.toTypedArray()
        launcherUprawnien.launch(uprawnienia)
    }
    LaunchedEffect(stan.wyslano) {
        if (stan.wyslano){
            onZgloszeniaWyslano()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Zgłoś zdarzenie", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        if (!przyznanaLokalizacja) {
            Text("Aplikacja potrzebuje dostępu do lokalizacji, żeby zgłosić zdarzenie.")
            Spacer(Modifier.height(16.dp))
        }
        ExposedDropdownMenuBox(
            expanded = rozwinietyDropdown,
            onExpandedChange = { rozwinietyDropdown = it}
        ) {
            OutlinedTextField(
                value = stan.wybranaKategoria?.name?: "Wybierz kategorię",
                onValueChange = {},
                readOnly = true,
                label = {Text("Kategoria zagrożenia")},
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = rozwinietyDropdown,
                onDismissRequest = {rozwinietyDropdown = false}
            ) {
                stan.listaKategorii.forEach { typ->
                    DropdownMenuItem(
                        text = {Text(typ.name)},
                        onClick = {
                            viewModel.wybierzTyp(typ)
                            rozwinietyDropdown = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (stan.szerokoscGeo != null && stan.dlugoscGeo != null)
                "Lokalizacja: ${"%.5f".format(stan.szerokoscGeo)}, ${"%.5f".format(stan.dlugoscGeo)}"
            else "Pobieranie lokalizacji..."
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val uri = stworzUriNaZdjecie(context)
            zdjecieUri = uri
            launcherAparatu.launch(uri)
        }) {
            Text(if (stan.zdjeciaUri!= null) "Zdjęcie dołączone ✓ (zmień)" else "Zrób zdjęcie")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = stan.komentarz,
            onValueChange = { viewModel.ustawKomentarz(it) },
            label = { Text("Komentarz (opcjonalnie)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        stan.komunikatBledu?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = { viewModel.wyslijZgloszenie(uzytkownikId) },
            enabled = !stan.wysylanie,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (stan.wysylanie) "Wysyłanie..." else "Wyślij zgłoszenie")
        }
    }
}
@SuppressLint("MissingPermission")
private fun pobierzLokalizacje(
    context: Context,
    onSukces: (Double, Double) -> Unit,
    onBlad: () -> Unit
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val zadanie = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .setDurationMillis(15_000)       // limit czasu 15 s
        .setMaxUpdateAgeMillis(60_000)   // akceptuj pozycję sprzed max. minuty
        .build()

    client.getCurrentLocation(zadanie, null)
        .addOnSuccessListener { lokalizacja ->
            if (lokalizacja != null) {
                onSukces(lokalizacja.latitude, lokalizacja.longitude)
            } else {
                onBlad()
            }
        }
        .addOnFailureListener { onBlad() }
}
private fun stworzUriNaZdjecie(context: Context): Uri {
    val plik = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "zgloszenie_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", plik)
}