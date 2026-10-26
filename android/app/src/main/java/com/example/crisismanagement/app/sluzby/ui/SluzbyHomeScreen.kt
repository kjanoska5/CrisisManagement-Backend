package com.example.crisismanagement.app.sluzby.ui

import androidx.compose.runtime.Composable
import com.example.crisismanagement.app.sluzby.reports.ServiceMapScreen

@Composable
fun SluzbyHomeScreen(userId: String) {
    ServiceMapScreen(userId = userId)
}