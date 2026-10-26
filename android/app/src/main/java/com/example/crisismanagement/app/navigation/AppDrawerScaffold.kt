package com.example.crisismanagement.app.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.crisismanagement.R
import com.example.crisismanagement.app.enums.Rola
import com.example.crisismanagement.ui.theme.extendedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScaffold(
    role: Rola,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val primary = MaterialTheme.colorScheme.primary
    val danger = MaterialTheme.extendedColors.danger

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.currentValue == DrawerValue.Open ||
            currentDestination != AppDestination.MAP,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = primary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                AppDestination.entries
                    .filter { role in it.roles }
                    .forEach { destination ->
                        NavigationDrawerItem(
                            label = { Text(stringResource(destination.titleRes)) },
                            icon = {
                                Icon(destination.icon, contentDescription = null)
                            },
                            selected = destination == currentDestination,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(destination)
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = primary.copy(alpha = 0.16f),
                                selectedIconColor = primary,
                                selectedTextColor = primary
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.nav_logout)) },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = danger,
                        unselectedTextColor = danger
                    ),
                    modifier = Modifier.padding(
                        PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(currentDestination.titleRes)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.nav_open_menu)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            content = content
        )
    }
}