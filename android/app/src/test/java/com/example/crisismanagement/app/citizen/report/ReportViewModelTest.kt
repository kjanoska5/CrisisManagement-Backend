package com.example.crisismanagement.app.citizen.report

import com.example.crisismanagement.MainDispatcherRule
import com.example.crisismanagement.app.common.network.NewReportRequest
import com.example.crisismanagement.app.common.network.ReportsApi
import com.example.crisismanagement.app.common.network.ThreatType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = mockk<ReportsApi>()
    private val fireType = ThreatType(id = "threat-1", name = "Fire")
    private val userId = "user-1"

    @Before
    fun setUp() {
        coEvery { api.getThreatTypes() } returns listOf(fireType)
    }

    private fun createViewModel() = ReportViewModel(api)

    private fun httpException(code: Int, body: String) = HttpException(
        Response.error<Any>(code, body.toResponseBody("application/json".toMediaTypeOrNull()))
    )

    @Test
    fun `loads threat types when created`() {
        val viewModel = createViewModel()

        assertEquals(listOf(fireType), viewModel.uiState.value.categoriesList)
    }

    @Test
    fun `shows error when threat types cannot be loaded`() {
        coEvery { api.getThreatTypes() } throws IOException("no connection")

        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.categoriesList.isEmpty())
        assertEquals("no connection", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `sendReport without category is blocked and does not call api`() {
        val viewModel = createViewModel()
        viewModel.setLocation(51.7592, 19.456)

        viewModel.sendReport(userId)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSent)
        coVerify(exactly = 0) { api.sendReport(any()) }
    }

    @Test
    fun `sendReport without location is blocked and does not call api`() {
        val viewModel = createViewModel()
        viewModel.selectCategory(fireType)

        viewModel.sendReport(userId)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSent)
        coVerify(exactly = 0) { api.sendReport(any()) }
    }

    @Test
    fun `sendReport success sends correct request and sets sent state`() {
        coEvery { api.sendReport(any()) } just Runs
        val viewModel = createViewModel()
        viewModel.selectCategory(fireType)
        viewModel.setLocation(51.7592, 19.456)
        viewModel.setComment("Smoke near the entrance")

        viewModel.sendReport(userId)

        val state = viewModel.uiState.value
        assertTrue(state.isSent)
        assertFalse(state.isSending)
        assertNull(state.errorMessage)
        coVerify(exactly = 1) {
            api.sendReport(
                NewReportRequest(
                    userId = userId,
                    threatTypeId = "threat-1",
                    lat = 51.7592,
                    lng = 19.456,
                    photoUrl = null,
                    comment = "Smoke near the entrance"
                )
            )
        }
    }

    @Test
    fun `blank comment is sent as null`() {
        coEvery { api.sendReport(any()) } just Runs
        val viewModel = createViewModel()
        viewModel.selectCategory(fireType)
        viewModel.setLocation(51.7592, 19.456)
        viewModel.setComment("   ")

        viewModel.sendReport(userId)

        coVerify { api.sendReport(match { it.comment == null }) }
    }

    @Test
    fun `http error shows status code and does not set sent state`() {
        coEvery { api.sendReport(any()) } throws httpException(500, """{"message":"boom"}""")
        val viewModel = createViewModel()
        viewModel.selectCategory(fireType)
        viewModel.setLocation(51.7592, 19.456)

        viewModel.sendReport(userId)

        val state = viewModel.uiState.value
        assertFalse(state.isSent)
        assertFalse(state.isSending)
        assertTrue(state.errorMessage!!.contains("500"))
    }

    @Test
    fun `io error shows network message and does not set sent state`() {
        coEvery { api.sendReport(any()) } throws IOException("timeout")
        val viewModel = createViewModel()
        viewModel.selectCategory(fireType)
        viewModel.setLocation(51.7592, 19.456)

        viewModel.sendReport(userId)

        val state = viewModel.uiState.value
        assertFalse(state.isSent)
        assertFalse(state.isSending)
        assertTrue(state.errorMessage!!.contains("timeout"))
    }
}