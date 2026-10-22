package com.example.crisismanagement.app.common.network

sealed interface ConnectionState {
    data object Checking : ConnectionState
    data class Connected(val typesCount: Int) : ConnectionState
    data class Error(val message: String) : ConnectionState
}