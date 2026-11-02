package com.example.crisismanagement.app.common.network

import retrofit2.http.Body
import retrofit2.http.POST
data class GoogleLoginRequest(val idToken: String)

data class AuthResponse(val userId: String, val role: String)

interface AuthApi {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse
}