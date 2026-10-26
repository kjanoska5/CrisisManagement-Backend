package com.example.crisismanagement.app.sluzby.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.network.ReportSummary
import com.example.crisismanagement.app.common.network.RetrofitClient
import com.example.crisismanagement.app.common.network.ServiceCreateReportRequest
import com.example.crisismanagement.app.common.network.ServiceUpdateReportRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ServiceMapUiState(
    val reports: List<ReportSummary> = emptyList(),
    val categories: Map<String, String> = emptyMap(),
    val draftPosition: LatLng? = null,
    val selectedReport: ReportSummary? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ServiceMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceMapUiState())
    val uiState: StateFlow<ServiceMapUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            safeCall {
                val types = RetrofitClient.api.pobierzTypyZagrozen()
                _uiState.update { state ->
                    state.copy(categories = types.associate { it.id.toString() to it.name })
                }
                refreshReports()
            }
        }
    }

    fun onMapTapped(position: LatLng) {
        _uiState.update { it.copy(draftPosition = position, selectedReport = null) }
    }

    fun dismissDraft() {
        _uiState.update { it.copy(draftPosition = null) }
    }

    fun onReportSelected(report: ReportSummary) {
        _uiState.update { it.copy(selectedReport = report, draftPosition = null) }
    }

    fun dismissSelected() {
        _uiState.update { it.copy(selectedReport = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun createReport(userId: String, threatTypeId: String, comment: String) {
        val position = _uiState.value.draftPosition ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            safeCall {
                RetrofitClient.api.createServiceReport(
                    ServiceCreateReportRequest(
                        userId = userId,
                        threatTypeId = threatTypeId,
                        lat = position.latitude,
                        lng = position.longitude,
                        comment = comment.ifBlank { null }
                    )
                )
                _uiState.update { it.copy(draftPosition = null) }
                refreshReports()
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun updateReport(threatTypeId: String, comment: String) {
        val report = _uiState.value.selectedReport ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            safeCall {
                RetrofitClient.api.updateReport(
                    id = report.id,
                    request = ServiceUpdateReportRequest(
                        threatTypeId = threatTypeId,
                        lat = report.lat,
                        lng = report.lng,
                        comment = comment.ifBlank { null }
                    )
                )
                _uiState.update { it.copy(selectedReport = null) }
                refreshReports()
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun deleteReport() {
        val report = _uiState.value.selectedReport ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            safeCall {
                val response = RetrofitClient.api.deleteReport(report.id)
                if (!response.isSuccessful) throw HttpException(response)
                _uiState.update { it.copy(selectedReport = null) }
                refreshReports()
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    private suspend fun refreshReports() {
        val reports = RetrofitClient.api.getReports()
        _uiState.update { it.copy(reports = reports) }
    }

    private suspend fun safeCall(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            setError("Server error (code: ${e.code()})")
        } catch (e: IOException) {
            setError("Network error: server unreachable or timed out")
        } catch (e: Exception) {
            setError("Unexpected error: ${e.message ?: "unknown"}")
        }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}