package com.example.crisismanagement.app.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.auth.UserSession
import com.example.crisismanagement.app.common.auth.toAppRole
import com.example.crisismanagement.app.common.network.AuthApi
import com.example.crisismanagement.app.common.network.GoogleLoginRequest
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
    NETWORK,
    SERVER,
    UNKNOWN_ROLE
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: LoginError? = null,
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

    fun loginWithGoogleToken(idToken: String, displayName: String?, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.loginWithGoogle(GoogleLoginRequest(idToken))
                val role = response.role.toAppRole()
                //do usuniecia pozniej
                Log.d("LoginViewModel", "Logged in as userId=${response.userId} role=${response.role}")
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
                                signedInWithGoogle = true
                            )
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                showError(
                    when (e.code()) {
                        401 -> LoginError.INVALID_TOKEN
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
}