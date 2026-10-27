package com.example.crisismanagement.app.common.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.network.ReportSummary
import com.example.crisismanagement.app.common.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MapUiState(
    val reports: List<ReportSummary> = emptyList(),
    val categories: Map<String, String> = emptyMap(),
    val chosenReport: ReportSummary? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class ThreatMapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private fun startAutoRefreshing() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    val result = RetrofitClient.api.getReports()
                    val types = RetrofitClient.api.getThreatTypes()
                    val mapTypes = types.associate { it.id to it.name }
                    _uiState.value = _uiState.value.copy(reports = result, categories = mapTypes, error = null)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                delay(10000)
            }
        }
    }

    init {
        startAutoRefreshing()
    }

    fun chooseReport(report: ReportSummary?) {
        _uiState.value = _uiState.value.copy(chosenReport = report)
    }
}
