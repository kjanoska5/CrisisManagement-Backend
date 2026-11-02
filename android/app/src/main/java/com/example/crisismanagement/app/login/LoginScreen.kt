package com.example.crisismanagement.app.login

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.BuildConfig
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.auth.GoogleAuthConfig
import com.example.crisismanagement.app.common.auth.GoogleIdTokenProvider
import com.example.crisismanagement.app.common.auth.GoogleSignInResult
import com.example.crisismanagement.app.common.auth.TEST_USER_ID
import com.example.crisismanagement.app.common.auth.UserSession
import com.example.crisismanagement.app.common.components.AppTextField
import com.example.crisismanagement.app.common.components.ConnectionStatus
import com.example.crisismanagement.app.common.components.PrimaryButton
import com.example.crisismanagement.app.common.components.SecondaryButton
import com.example.crisismanagement.app.common.network.ConnectionViewModel
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.ui.theme.extendedColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoggedIn: (UserSession) -> Unit,
    onOpenRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(),
    connectionViewModel: ConnectionViewModel = viewModel()
) {
    val loginState by loginViewModel.uiState.collectAsState()
    val connectionState by connectionViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenProvider = remember { GoogleIdTokenProvider() }

    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState.session) {
        loginState.session?.let(onLoggedIn)
    }

    val onGoogleClick: () -> Unit = {
        if (!GoogleAuthConfig.isConfigured) {
            loginViewModel.showError(LoginError.NOT_CONFIGURED)
        } else {
            scope.launch {
                when (val result = tokenProvider.requestIdToken(context)) {
                    is GoogleSignInResult.Success ->
                        loginViewModel.loginWithGoogleToken(result.idToken, result.displayName, result.email)
                    GoogleSignInResult.Cancelled -> Unit
                    is GoogleSignInResult.Failure -> {
                        Log.w("LoginScreen", "Google sign-in failed: ${result.message}")
                        loginViewModel.showError(LoginError.GOOGLE_FAILED)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.extendedColors.textMuted
        )
        Spacer(Modifier.height(16.dp))
        ConnectionStatus(
            state = connectionState,
            onRetry = { connectionViewModel.checkConnection() }
        )
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppTextField(
                label = stringResource(R.string.login_email_label),
                value = email,
                onValueChange = {
                    email = it
                    loginViewModel.clearErrors()
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.login_email_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                errorText = loginState.emailError?.let { stringResource(it.messageRes()) }
            )
            AppTextField(
                label = stringResource(R.string.login_password_label),
                value = password,
                onValueChange = {
                    email = it
                    loginViewModel.clearErrors()
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                errorText = loginState.passwordError?.let { stringResource(it.messageRes()) }
            )

            loginState.error?.let { error ->
                Text(
                    text = stringResource(error.messageRes()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.danger
                )
            }

            PrimaryButton(
                text = stringResource(
                    if (loginState.isLoading) R.string.login_signing_in else R.string.login_submit
                ),
                onClick = { loginViewModel.loginWithEmail(email, password) },
                enabled = !loginState.isLoading
            )

            Text(
                text = stringResource(R.string.login_or),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = stringResource(R.string.login_google),
                onClick = onGoogleClick,
                enabled = !loginState.isLoading
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textMuted
                )
                TextButton(onClick = onOpenRegister) {
                    Text(stringResource(R.string.login_register_link))
                }
            }

            SecondaryButton(
                text = stringResource(R.string.login_services_test),
                onClick = { onLoggedIn(UserSession(TEST_USER_ID, Role.SERVICES)) },
                enabled = !loginState.isLoading
            )
            Text(
                text = stringResource(R.string.login_services_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (BuildConfig.DEBUG) {
                SecondaryButton(
                    text = stringResource(R.string.login_citizen_test),
                    onClick = { onLoggedIn(UserSession(TEST_USER_ID, Role.CITIZEN)) },
                    enabled = !loginState.isLoading
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.login_emergency_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extendedColors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

@StringRes
private fun LoginError.messageRes(): Int = when (this) {
    LoginError.NOT_CONFIGURED -> R.string.login_error_not_configured
    LoginError.GOOGLE_FAILED -> R.string.login_error_google_failed
    LoginError.INVALID_TOKEN -> R.string.login_error_invalid_token
    LoginError.ACCOUNT_CONFLICT -> R.string.login_error_conflict
    LoginError.INVALID_CREDENTIALS -> R.string.login_error_invalid_credentials
    LoginError.NETWORK -> R.string.login_error_network
    LoginError.SERVER -> R.string.login_error_server
    LoginError.UNKNOWN_ROLE -> R.string.login_error_unknown_role
}