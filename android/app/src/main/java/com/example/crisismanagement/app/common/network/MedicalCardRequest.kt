package com.example.crisismanagement.app.common.network

// dane z formularza do wysylki
data class MedicalCardRequest(
    val bloodType: String?,
    val allergies: String?,
    val chronicDiseases: String?,
    val emergencyContact: String?,
    val medications: String?
)