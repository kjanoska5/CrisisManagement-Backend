package com.example.crisismanagement.app.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.app.common.components.ConnectionStatus
import com.example.crisismanagement.app.common.network.ConnectionViewModel
import com.example.crisismanagement.app.enums.Role

@Composable
fun LoginScreen(
    onLoggedIn: (Role) -> Unit,
    connectionViewModel: ConnectionViewModel = viewModel()
) {
    val connectionState by connectionViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConnectionStatus(
            state = connectionState,
            onRetry = { connectionViewModel.checkConnection() }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Sign in (test version)")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { onLoggedIn(Role.CITIZEN) }) {
            Text("Log in as Citizen")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onLoggedIn(Role.SERVICES) }) {
            Text("Log in as Services")
        }
    }
}
