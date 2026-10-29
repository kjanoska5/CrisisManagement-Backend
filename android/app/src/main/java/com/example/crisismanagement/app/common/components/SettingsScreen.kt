package com.example.crisismanagement.app.common.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.settings.ThemeMode
import com.example.crisismanagement.app.common.settings.ThemeViewModel
import com.example.crisismanagement.ui.theme.extendedColors

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    )
) {
    val themeMode by themeViewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.selectableGroup()) {
                Text(
                    text = stringResource(R.string.settings_theme),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )
                ThemeMode.entries.forEach { mode ->
                    ThemeOptionRow(
                        label = stringResource(mode.labelRes()),
                        selected = mode == themeMode,
                        onSelect = { themeViewModel.setThemeMode(mode) }
                    )
                }
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingsRow(stringResource(R.string.settings_language))
            }
        }
    }
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.theme_system
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.extendedColors.textMuted
            )
        )
        Spacer(Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

// Placeholder na wybor jezyka
@Composable
private fun SettingsRow(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.coming_soon),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extendedColors.textMuted
        )
    }
}