package com.example.crisismanagement.app.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.crisismanagement.R
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.ui.theme.extendedColors

@Composable
fun ProfileScreen(
    role: Role,
    onOpenMedicalCard: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.extendedColors.surfaceHigh)
                        .padding(12.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(
                            if (role == Role.SERVICES) R.string.profile_role_service
                            else R.string.profile_role_citizen
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.profile_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textMuted
                    )
                }
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (role == Role.CITIZEN) {
                    ProfileRow(stringResource(R.string.title_medical_card), onOpenMedicalCard)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
                ProfileRow(stringResource(R.string.nav_settings), onOpenSettings)
            }
        }

        SecondaryButton(
            text = stringResource(R.string.nav_logout),
            onClick = onLogout
        )
    }
}

@Composable
private fun ProfileRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.textMuted
        )
    }
}
