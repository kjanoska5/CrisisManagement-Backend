package com.example.crisismanagement.app.login

import com.example.crisismanagement.MainDispatcherRule
import com.example.crisismanagement.app.common.network.AuthApi
import com.example.crisismanagement.app.common.network.AuthResponse
import com.example.crisismanagement.app.common.network.LoginRequest
import com.example.crisismanagement.app.enums.Role
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = mockk<AuthApi>()
    private val viewModel = LoginViewModel(api)

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, "".toResponseBody("application/json".toMediaType()))
    )

    @Test
    fun `empty fields do not call the API`() = runTest {
        viewModel.loginWithEmail("", "")
        advanceUntilIdle()

        assertEquals(FieldError.EMPTY, viewModel.uiState.value.emailError)
        assertEquals(FieldError.EMPTY, viewModel.uiState.value.passwordError)
        coVerify(exactly = 0) { api.login(any()) }
    }

    @Test
    fun `success creates a local session with normalized email`() = runTest {
        val request = slot<LoginRequest>()
        coEvery { api.login(capture(request)) } returns AuthResponse("id-1", "CITIZEN")

        viewModel.loginWithEmail(" Test@Okej.com ", "password1")
        advanceUntilIdle()

        assertEquals("test@okej.com", request.captured.email)
        val session = viewModel.uiState.value.session
        assertEquals("id-1", session?.userId)
        assertEquals(Role.CITIZEN, session?.role)
        assertEquals("test@okej.com", session?.email)
        assertNull(session?.displayName)
        assertFalse(session?.signedInWithGoogle ?: true)
    }

    @Test
    fun `401 maps to INVALID_CREDENTIALS`() = runTest {
        coEvery { api.login(any()) } throws httpError(401)

        viewModel.loginWithEmail("test@okej.com", "bad-password")
        advanceUntilIdle()

        assertEquals(LoginError.INVALID_CREDENTIALS, viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.session)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `IOException maps to NETWORK`() = runTest {
        coEvery { api.login(any()) } throws IOException("offline")

        viewModel.loginWithEmail("test@okej.com", "password1")
        advanceUntilIdle()

        assertEquals(LoginError.NETWORK, viewModel.uiState.value.error)
    }

    @Test
    fun `clearErrors removes error and field errors`() = runTest {
        viewModel.loginWithEmail("", "")
        viewModel.clearErrors()

        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.passwordError)
        assertNull(viewModel.uiState.value.error)
    }
}