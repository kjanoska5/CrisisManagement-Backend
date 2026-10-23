package com.example.crisismanagement.app.common.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ConnectionViewModel: ViewModel(){
    private val _uiState = MutableStateFlow<ConnectionState>(ConnectionState.Checking)
    val uiState: StateFlow<ConnectionState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        checkConnection()
    }

    fun checkConnection() {
        _uiState.value = ConnectionState.Checking
        startAutoRefreshing()
    }

    private fun startAutoRefreshing(){
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                performCheck()
                delay(10000)
            }
        }
    }
    private suspend fun performCheck(){
        try {
            val threatTypes = RetrofitClient.api.pobierzTypyZagrozen()
            _uiState.value = ConnectionState.Connected(typesCount = threatTypes.size)
        } catch (e: HttpException) {
            _uiState.value = ConnectionState.Error(message = "Server error (code: ${e.code()})")
        }
        catch (e: IOException) {
            _uiState.value = ConnectionState.Error(message = "Network error: Server unreachable or timed out")
        }
        catch (e: Exception) {
            _uiState.value = ConnectionState.Error(message = "Unexpected error: ${e.message?: "Unknown"}")
        }
    }

}