package com.example.crisismanagement.app.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.auth.UserSession
import com.example.crisismanagement.app.common.auth.toAppRole
import com.example.crisismanagement.app.common.network.AuthApi
import com.example.crisismanagement.app.common.network.AuthResponse
import com.example.crisismanagement.app.common.network.GoogleLoginRequest
import com.example.crisismanagement.app.common.network.LoginRequest
import com.example.crisismanagement.app.common.network.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

enum class LoginError {
    NOT_CONFIGURED,
    GOOGLE_FAILED,
    INVALID_TOKEN,
    ACCOUNT_CONFLICT,
    INVALID_CREDENTIALS,
    NETWORK,
    SERVER,
    UNKNOWN_ROLE
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: LoginError? = null,
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val session: UserSession? = null
)

class LoginViewModel(
    private val authApi: AuthApi = RetrofitClient.authApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun showError(error: LoginError) {
        _uiState.update { it.copy(isLoading = false, error = error) }
    }

    fun loginWithEmail(email: String, password: String) {
        val emailError = AuthValidation.validateEmail(email)
        val passwordError = AuthValidation.validateLoginPassword(password)
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(error = null, emailError = emailError, passwordError = passwordError)
            }
            return
        }
        val normalizedEmail = email.trim().lowercase()
        launchAuth(
            unauthorizedError = LoginError.INVALID_CREDENTIALS,
            call = { authApi.login(LoginRequest(normalizedEmail, password)) },
            onSuccess = { response ->
                completeLogin(response, displayName = null, email = normalizedEmail, signedInWithGoogle = false)
            }
        )
    }

    fun loginWithGoogleToken(idToken: String, displayName: String?, email: String) {
        launchAuth(
            unauthorizedError = LoginError.INVALID_TOKEN,
            call = { authApi.loginWithGoogle(GoogleLoginRequest(idToken)) },
            onSuccess = { response ->
                completeLogin(response, displayName = displayName, email = email, signedInWithGoogle = true)
            }
        )
    }

    private fun launchAuth(
        unauthorizedError: LoginError,
        call: suspend () -> AuthResponse,
        onSuccess: (AuthResponse) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, emailError = null, passwordError = null)
            }
            try {
                onSuccess(call())
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                showError(
                    when (e.code()) {
                        401 -> unauthorizedError
                        409 -> LoginError.ACCOUNT_CONFLICT
                        else -> LoginError.SERVER
                    }
                )
            } catch (e: IOException) {
                showError(LoginError.NETWORK)
            } catch (e: Exception) {
                showError(LoginError.SERVER)
            }
        }
    }

    private fun completeLogin(
        response: AuthResponse,
        displayName: String?,
        email: String,
        signedInWithGoogle: Boolean
    ) {
        val role = response.role.toAppRole()
        if (role == null) {
            Log.w("LoginViewModel", "Unknown role from server: '${response.role}'")
            showError(LoginError.UNKNOWN_ROLE)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    session = UserSession(
                        userId = response.userId,
                        role = role,
                        displayName = displayName,
                        email = email,
                        signedInWithGoogle = signedInWithGoogle
                    )
                )
            }
        }
    }
    fun clearErrors() {
        _uiState.update { it.copy(error = null, emailError = null, passwordError = null) }
    }
}