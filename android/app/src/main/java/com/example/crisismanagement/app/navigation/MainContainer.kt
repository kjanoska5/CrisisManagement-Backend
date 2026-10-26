package com.example.crisismanagement.app.common.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.crisismanagement.app.enums.Rola
import com.example.crisismanagement.app.navigation.AppDestination
import com.example.crisismanagement.app.navigation.AppDrawerScaffold
import com.example.crisismanagement.app.navigation.PlaceholderScreen
import com.example.crisismanagement.app.obywatel.medicalcard.MedicalCardScreen
import com.example.crisismanagement.app.obywatel.ui.ObywatelHomeScreen
import com.example.crisismanagement.app.sluzby.ui.SluzbyHomeScreen

@Composable
fun MainContainer(
    role: Rola,
    userId: String,
    onNewReport: () -> Unit,
    onLogout: () -> Unit
) {
    var destination by rememberSaveable { mutableStateOf(AppDestination.MAP) }
    BackHandler(enabled = destination != AppDestination.MAP) {
        destination = AppDestination.MAP
    }

    AppDrawerScaffold(
        role = role,
        currentDestination = destination,
        onNavigate = { destination = it },
        onLogout = onLogout
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (destination) {
                AppDestination.MAP -> when (role) {
                    Rola.OBYWATEL -> ObywatelHomeScreen(onNewReportClick = onNewReport)
                    Rola.SLUZBY -> SluzbyHomeScreen(userId = userId)
                }

                AppDestination.MEDICAL_CARD -> MedicalCardScreen(
                    userId = userId,
                    onSaved = { destination = AppDestination.MAP }
                )

                else -> PlaceholderScreen(title = stringResource(destination.titleRes))
            }
        }
    }
}