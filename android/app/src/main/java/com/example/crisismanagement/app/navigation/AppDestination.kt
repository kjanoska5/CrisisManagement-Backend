package com.example.crisismanagement.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.crisismanagement.R
import com.example.crisismanagement.app.enums.Rola

enum class AppDestination(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val roles: Set<Rola>
) {
    MAP(R.string.nav_map, Icons.Default.LocationOn, setOf(Rola.OBYWATEL, Rola.SLUZBY)),
    MY_REPORTS(R.string.nav_my_reports, Icons.AutoMirrored.Filled.List, setOf(Rola.OBYWATEL)),
    MEDICAL_CARD(R.string.nav_medical_card, Icons.Default.Favorite, setOf(Rola.OBYWATEL)),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings, setOf(Rola.OBYWATEL, Rola.SLUZBY))
}