package com.example.crisismanagement.app.common.auth

import com.example.crisismanagement.app.enums.Role

const val TEST_USER_ID = "a4bab6f2-60fa-461a-b7b5-746b0e883896"

data class UserSession(
    val userId: String,
    val role: Role,
    val displayName: String? = null,
    val email: String? = null,
    val signedInWithGoogle: Boolean = false
)

fun String.toAppRole(): Role? = when (trim().uppercase()) {
    "OBYWATEL", "CITIZEN" -> Role.CITIZEN
    "SLUZBY", "SERVICE", "SERVICES" -> Role.SERVICES
    else -> null
}