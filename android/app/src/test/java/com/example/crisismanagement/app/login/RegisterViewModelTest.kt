package com.example.crisismanagement.app.login

import com.example.crisismanagement.MainDispatcherRule
import com.example.crisismanagement.app.common.network.AuthApi
import com.example.crisismanagement.app.common.network.AuthResponse
import com.example.crisismanagement.app.common.network.RegisterRequest
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = mockk<AuthApi>()
    private val viewModel = RegisterViewModel(api)

    private fun httpError(code: Int) = HttpException(
        Response.error<Any>(code, "".toResponseBody("application/json".toMediaType()))
    )

    @Test
    fun `invalid form does not call the API and sets field errors`() = runTest {
        viewModel.register("Jan", "zly", "123", "456", Role.CITIZEN)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(FieldError.NAME_INCOMPLETE, state.nameError)
        assertEquals(FieldError.INVALID_EMAIL, state.emailError)
        assertEquals(FieldError.PASSWORD_TOO_SHORT, state.passwordError)
        assertEquals(FieldError.PASSWORDS_DIFFER, state.repeatPasswordError)
        coVerify(exactly = 0) { api.register(any()) }
    }

    @Test
    fun `success sends split name and normalized email and creates session`() = runTest {
        val request = slot<RegisterRequest>()
        coEvery { api.register(capture(request)) } returns AuthResponse("id-1", "CITIZEN")

        viewModel.register("Jan Kowalski", " Jan@Example.COM ", "password1", "password1", Role.CITIZEN)
        advanceUntilIdle()

        assertEquals("jan@example.com", request.captured.email)
        assertEquals("Jan", request.captured.firstName)
        assertEquals("Kowalski", request.captured.lastName)
        assertEquals("CITIZEN", request.captured.role)

        val session = viewModel.uiState.value.session
        assertNotNull(session)
        assertEquals("id-1", session?.userId)
        assertEquals(Role.CITIZEN, session?.role)
        assertEquals("Jan Kowalski", session?.displayName)
        assertEquals("jan@example.com", session?.email)
    }

    @Test
    fun `services role is sent as SERVICES`() = runTest {
        val request = slot<RegisterRequest>()
        coEvery { api.register(capture(request)) } returns AuthResponse("id-2", "SERVICES")

        viewModel.register("Jan Kowalski", "s@example.com", "password1", "password1", Role.SERVICES)
        advanceUntilIdle()

        assertEquals("SERVICES", request.captured.role)
        assertEquals(Role.SERVICES, viewModel.uiState.value.session?.role)
    }

    @Test
    fun `409 maps to EMAIL_TAKEN`() = runTest {
        coEvery { api.register(any()) } throws httpError(409)

        viewModel.register("Jan Kowalski", "jan@example.com", "password1", "password1", Role.CITIZEN)
        advanceUntilIdle()

        assertEquals(RegisterError.EMAIL_TAKEN, viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.session)
    }

    @Test
    fun `500 maps to SERVER and IOException to NETWORK`() = runTest {
        coEvery { api.register(any()) } throws httpError(500)
        viewModel.register("Jan Kowalski", "jan@example.com", "password1", "password1", Role.CITIZEN)
        advanceUntilIdle()
        assertEquals(RegisterError.SERVER, viewModel.uiState.value.error)

        coEvery { api.register(any()) } throws IOException("offline")
        viewModel.register("Jan Kowalski", "jan@example.com", "password1", "password1", Role.CITIZEN)
        advanceUntilIdle()
        assertEquals(RegisterError.NETWORK, viewModel.uiState.value.error)
    }
}