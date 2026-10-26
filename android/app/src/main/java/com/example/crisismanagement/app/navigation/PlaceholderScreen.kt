package com.example.crisismanagement.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.crisismanagement.R
import com.example.crisismanagement.ui.theme.extendedColors

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "$title\n${stringResource(R.string.coming_soon)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.extendedColors.textMuted
        )
    }
}