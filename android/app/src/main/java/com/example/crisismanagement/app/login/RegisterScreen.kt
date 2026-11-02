package com.example.crisismanagement.app.login

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crisismanagement.R
import com.example.crisismanagement.app.common.auth.UserSession
import com.example.crisismanagement.app.common.components.AppCard
import com.example.crisismanagement.app.common.components.AppTextField
import com.example.crisismanagement.app.common.components.PrimaryButton
import com.example.crisismanagement.app.common.components.SecondaryButton
import com.example.crisismanagement.app.enums.Role
import com.example.crisismanagement.ui.theme.extendedColors

@Composable
fun RegisterScreen(
    onRegistered: (UserSession) -> Unit,
    onBackToLogin: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val state by registerViewModel.uiState.collectAsState()

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf(Role.CITIZEN) }

    LaunchedEffect(state.session) {
        state.session?.let(onRegistered)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
            )
        }

        Text(
            text = stringResource(R.string.register_heading),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.register_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textMuted
        )

        AppTextField(
            label = stringResource(R.string.register_name_label),
            value = fullName,
            onValueChange = {
                fullName = it
                registerViewModel.clearErrors()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.register_name_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            errorText = state.nameError?.let { stringResource(it.messageRes()) }
        )
        AppTextField(
            label = stringResource(R.string.login_email_label),
            value = email,
            onValueChange = { email = it
                registerViewModel.clearErrors()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.login_email_placeholder),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            errorText = state.emailError?.let { stringResource(it.messageRes()) }
        )
        AppTextField(
            label = stringResource(R.string.login_password_label),
            value = password,
            onValueChange = { password = it
                registerViewModel.clearErrors()
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation(),
            errorText = state.passwordError?.let { stringResource(it.messageRes()) }
        )
        AppTextField(
            label = stringResource(R.string.register_password_repeat_label),
            value = repeatPassword,
            onValueChange = { repeatPassword = it
                registerViewModel.clearErrors()
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            errorText = state.repeatPasswordError?.let { stringResource(it.messageRes()) }
        )

        Text(
            text = stringResource(R.string.register_role_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.extendedColors.textMuted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoleOption(
                text = stringResource(R.string.register_role_citizen),
                selected = role == Role.CITIZEN,
                onClick = { role = Role.CITIZEN },
                modifier = Modifier.weight(1f)
            )
            RoleOption(
                text = stringResource(R.string.register_role_services),
                selected = role == Role.SERVICES,
                onClick = { role = Role.SERVICES },
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = stringResource(R.string.register_services_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extendedColors.textMuted
        )

        state.error?.let { error ->
            Text(
                text = stringResource(error.messageRes()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.danger
            )
        }

        PrimaryButton(
            text = stringResource(
                if (state.isLoading) R.string.register_submitting else R.string.register_submit
            ),
            onClick = { registerViewModel.register(fullName, email, password, repeatPassword, role) },
            enabled = !state.isLoading
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.register_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textMuted
            )
            TextButton(onClick = onBackToLogin) {
                Text(stringResource(R.string.register_login_link))
            }
        }
    }
}

@Composable
private fun RoleOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        PrimaryButton(text = text, onClick = onClick, modifier = modifier)
    } else {
        SecondaryButton(text = text, onClick = onClick, modifier = modifier)
    }
}

@StringRes
private fun RegisterError.messageRes(): Int = when (this) {
    RegisterError.EMAIL_TAKEN -> R.string.register_error_email_taken
    RegisterError.INVALID_INPUT -> R.string.register_error_invalid_input
    RegisterError.NETWORK -> R.string.login_error_network
    RegisterError.SERVER -> R.string.login_error_server
    RegisterError.UNKNOWN_ROLE -> R.string.login_error_unknown_role
}