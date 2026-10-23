package com.example.crisismanagement.app.obywatel.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crisismanagement.app.common.components.MapaWidok
import com.example.crisismanagement.app.common.components.MapaZagrozenViewModel
import com.example.crisismanagement.app.navigation.Route

@Composable
fun ObywatelHomeScreen(
    navController: NavHostController,
    mapaZagrozenViewModel: MapaZagrozenViewModel = viewModel()) {
    val mapState by mapaZagrozenViewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok(
            reports = mapState.reports,
            chosenReport = mapState.chosenReports,
            categories = mapState.categories,
            onReportClick = {mapaZagrozenViewModel.chooseReport(it)},
            onDismissDialog = {mapaZagrozenViewModel.chooseReport(null)},
            contentPadding = PaddingValues(bottom = 88.dp)
        )

        SmallFloatingActionButton(
            onClick = { navController.navigate(Route.MedicalCard.route) },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Karta medyczna")
        }

        FloatingActionButton(
            onClick = { navController.navigate(Route.ZgloszenieForm.route) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Zgłoś zdarzenie")
        }
        // TODO: przycisk SOS (issue 6.1)
    }
}