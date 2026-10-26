package com.example.crisismanagement.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.crisismanagement.R

enum class AppDestination(
    @StringRes val tabLabelRes: Int,
    val icon: ImageVector
) {
    MAP(R.string.nav_tab_map, Icons.Default.LocationOn),
    REPORTS(R.string.nav_tab_reports, Icons.Default.Warning),
    PROFILE(R.string.nav_tab_profile, Icons.Default.Person)
}