package com.example.crisismanagement.app.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.auth.UserSession
import com.example.crisismanagement.app.common.auth.toAppRole
import com.example.crisismanagement.app.common.network.AuthApi
import com.example.crisismanagement.app.common.network.RegisterRequest
import com.example.crisismanagement.app.common.network.RetrofitClient
import com.example.crisismanagement.app.enums.Role
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

enum class RegisterError {
    EMAIL_TAKEN,
    INVALID_INPUT,
    NETWORK,
    SERVER,
    UNKNOWN_ROLE
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val nameError: FieldError? = null,
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val repeatPasswordError: FieldError? = null,
    val error: RegisterError? = null,
    val session: UserSession? = null
)

class RegisterViewModel(
    private val authApi: AuthApi = RetrofitClient.authApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private fun showError(error: RegisterError) {
        _uiState.update { it.copy(isLoading = false, error = error) }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        repeatPassword: String,
        role: Role
    ) {
        val nameError = AuthValidation.validateFullName(fullName)
        val emailError = AuthValidation.validateEmail(email)
        val passwordError = AuthValidation.validateNewPassword(password)
        val repeatError = AuthValidation.validatePasswordRepeat(password, repeatPassword)

        if (nameError != null || emailError != null || passwordError != null || repeatError != null) {
            _uiState.update {
                it.copy(
                    error = null,
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    repeatPasswordError = repeatError
                )
            }
            return
        }

        val (firstName, lastName) = AuthValidation.splitFullName(fullName) ?: return
        val normalizedEmail = email.trim().lowercase()
        val displayName = "$firstName $lastName"
        val request = RegisterRequest(
            email = normalizedEmail,
            password = password,
            firstName = firstName,
            lastName = lastName,
            role = role.toApiValue()
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    nameError = null,
                    emailError = null,
                    passwordError = null,
                    repeatPasswordError = null
                )
            }
            try {
                val response = authApi.register(request)
                val appRole = response.role.toAppRole()
                if (appRole == null) {
                    Log.w("RegisterViewModel", "Unknown role from server: '${response.role}'")
                    showError(RegisterError.UNKNOWN_ROLE)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = UserSession(
                                userId = response.userId,
                                role = appRole,
                                displayName = displayName,
                                email = normalizedEmail,
                                signedInWithGoogle = false
                            )
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                showError(
                    when (e.code()) {
                        409 -> RegisterError.EMAIL_TAKEN
                        400 -> RegisterError.INVALID_INPUT
                        else -> RegisterError.SERVER
                    }
                )
            } catch (e: IOException) {
                showError(RegisterError.NETWORK)
            } catch (e: Exception) {
                showError(RegisterError.SERVER)
            }
        }
    }
    fun clearErrors() {
        _uiState.update {
            it.copy(
                error = null,
                nameError = null,
                emailError = null,
                passwordError = null,
                repeatPasswordError = null
            )
        }
    }
}

private fun Role.toApiValue(): String = when (this) {
    Role.CITIZEN -> "CITIZEN"
    Role.SERVICES -> "SERVICES"
}