package com.example.crisismanagement.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.R
import com.example.crisismanagement.app.citizen.medicalcard.MedicalCardScreen
import com.example.crisismanagement.app.citizen.report.ReportScreen
import com.example.crisismanagement.app.citizen.report.ReportViewModel
import com.example.crisismanagement.app.citizen.ui.CitizenHomeScreen
import com.example.crisismanagement.app.common.components.ProfileScreen
import com.example.crisismanagement.app.common.components.SettingsScreen
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.app.services.ui.ServicesHomeScreen

enum class ProfileSection { HOME, MEDICAL_CARD, SETTINGS }

@Composable
fun MainContainer(
    role: Role,
    userId: String,
    onLogout: () -> Unit
) {
    var destination by rememberSaveable { mutableStateOf(AppDestination.MAP) }
    var profileSection by rememberSaveable { mutableStateOf(ProfileSection.HOME) }
    // New key = fresh form state (otherwise the "sent" flag would survive between visits)
    var reportFormSession by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = destination != AppDestination.MAP) {
        if (destination == AppDestination.PROFILE && profileSection != ProfileSection.HOME) {
            profileSection = ProfileSection.HOME
        } else {
            destination = AppDestination.MAP
        }
    }

    val isService = role == Role.SERVICES
    val title = when (destination) {
        AppDestination.MAP -> stringResource(
            if (isService) R.string.title_service_map else R.string.title_map
        )
        AppDestination.REPORTS -> stringResource(
            if (isService) R.string.title_details else R.string.title_report
        )
        AppDestination.PROFILE -> stringResource(
            when (profileSection) {
                ProfileSection.HOME -> R.string.title_profile
                ProfileSection.MEDICAL_CARD -> R.string.title_medical_card
                ProfileSection.SETTINGS -> R.string.nav_settings
            }
        )
    }

    AppScaffold(
        title = title,
        currentDestination = destination,
        onNavigate = {
            profileSection = ProfileSection.HOME
            destination = it
        }
    ) { padding ->
        val contentModifier = if (destination == AppDestination.MAP) {
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        } else {
            Modifier.padding(padding)
        }

        Box(modifier = contentModifier) {
            when (destination) {
                AppDestination.MAP -> when (role) {
                    Role.CITIZEN -> CitizenHomeScreen(
                        onNewReportClick = { destination = AppDestination.REPORTS }
                    )
                    Role.SERVICES -> ServicesHomeScreen(userId = userId)
                }

                AppDestination.REPORTS -> when (role) {
                    Role.CITIZEN -> ReportScreen(
                        userId = userId,
                        onReportSent = {
                            reportFormSession++
                            destination = AppDestination.MAP
                        },
                        viewModel = viewModel<ReportViewModel>(
                            key = "report-form-$reportFormSession"
                        )
                    )
                    Role.SERVICES -> PlaceholderScreen(title = title)
                }

                AppDestination.PROFILE -> when (profileSection) {
                    ProfileSection.HOME -> ProfileScreen(
                        role = role,
                        onOpenMedicalCard = { profileSection = ProfileSection.MEDICAL_CARD },
                        onOpenSettings = { profileSection = ProfileSection.SETTINGS },
                        onLogout = onLogout
                    )
                    ProfileSection.MEDICAL_CARD -> MedicalCardScreen(
                        userId = userId,
                        onSaved = { profileSection = ProfileSection.HOME }
                    )
                    ProfileSection.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}
