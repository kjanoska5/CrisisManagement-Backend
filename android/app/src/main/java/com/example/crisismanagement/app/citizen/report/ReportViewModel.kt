package com.example.crisismanagement.app.citizen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisismanagement.app.common.network.NewReportRequest
import com.example.crisismanagement.app.common.network.ReportsApi
import com.example.crisismanagement.app.common.network.RetrofitClient
import com.example.crisismanagement.app.common.network.ThreatType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ReportUiState(
    val categoriesList: List<ThreatType> = emptyList(),
    val selectedCategory: ThreatType? = null,
    val comment: String = "",
    val photoUri: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val isSending: Boolean = false,
    val isSent: Boolean = false,
    val errorMessage: String? = null
)

class ReportViewModel(
    private val api: ReportsApi = RetrofitClient.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private fun fetchThreatTypes() {
        viewModelScope.launch {
            try {
                val result = api.getThreatTypes()
                _uiState.value = _uiState.value.copy(categoriesList = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    init {
        fetchThreatTypes()
    }

    fun selectCategory(type: ThreatType) {
        _uiState.value = _uiState.value.copy(selectedCategory = type)
    }

    fun setComment(text: String) {
        _uiState.value = _uiState.value.copy(comment = text)
    }

    fun setPhotoUri(uri: String) {
        _uiState.value = _uiState.value.copy(photoUri = uri)
    }

    fun setLocation(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(lat = latitude, lng = longitude)
    }

    fun setError(text: String) {
        _uiState.value = _uiState.value.copy(errorMessage = text)
    }

    fun sendReport(userId: String) {
        val state = _uiState.value
        val category = state.selectedCategory
        val latitude = state.lat
        val longitude = state.lng
        if (category == null || latitude == null || longitude == null) {
            _uiState.value = state.copy(errorMessage = "Select a category and wait for location")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSending = true, errorMessage = null)
            try {
                api.sendReport(
                    NewReportRequest(
                        userId = userId,
                        threatTypeId = category.id,
                        lat = latitude,
                        lng = longitude,
                        photoUrl = null,
                        comment = state.comment.ifBlank { null }
                    )
                )
                _uiState.value = _uiState.value.copy(isSending = false, isSent = true)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    errorMessage = "Send error: ${e.response()?.errorBody()?.string()} ${e.code()}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    errorMessage = "Send error: ${e.message}"
                )
            }
        }
    }
}
