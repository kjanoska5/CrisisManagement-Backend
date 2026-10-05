package com.example.crisismanagement.app.sluzby.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.app.common.components.MapaWidok
import com.example.crisismanagement.app.common.components.MapaZagrozenViewModel

/**
 * Ekran startowy modułu służb.
 * Współdzieli MapaWidok() z modułem obywatela — dokładnie ten sam
 * komponent, inne funkcje dobudowane wokół niego (issue 3.1).
 */
@Composable
fun SluzbyHomeScreen(
    mapaZagrozenViewModel: MapaZagrozenViewModel = viewModel()
) {
    val mapState by mapaZagrozenViewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        MapaWidok(
            reports = mapState.reports,
            chosenReport = mapState.chosenReports,
            categories = mapState.categories,
            onReportClick = {mapaZagrozenViewModel.chooseReport(it)},
            onDismissDialog = {mapaZagrozenViewModel.chooseReport(null)}
        )
        // TODO: oznaczanie zdarzeń (issue 3.1), wysyłka alertów (issue 5.1)
    }
}