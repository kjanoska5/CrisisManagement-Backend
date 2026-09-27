package com.example.crisismanagement.app.common.network

// dane odbierane z backendu
data class MedicalCardResponse(
    val id: Long,
    val bloodType: String?,
    val allergies: String?,
    val chronicDiseases: String?,
    val emergencyContact: String?,
    val medications: String?
)