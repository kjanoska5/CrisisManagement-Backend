package com.example.crisismanagement.app.common.network

data class NewReportRequest(
    val userId: String,
    val threatTypeId: String,
    val lat: Double,
    val lng: Double,
    val photoUrl: String?,
    val comment: String?
)