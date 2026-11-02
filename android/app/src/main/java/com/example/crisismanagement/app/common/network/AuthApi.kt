package com.example.crisismanagement.app.common.network

import retrofit2.http.Body
import retrofit2.http.POST

data class GoogleLoginRequest(val idToken: String)

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val role: String
)

data class AuthResponse(val userId: String, val role: String)

interface AuthApi {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}