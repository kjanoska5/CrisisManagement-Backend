package com.example.crisismanagement.app.common.network

data class ServiceCreateReportRequest(
    val userId: String,
    val threatTypeId: String,
    val lat: Double,
    val lng: Double,
    val comment: String?,
    val source: String = "SERVICE"
)

data class ServiceUpdateReportRequest(
    val threatTypeId: String,
    val lat: Double,
    val lng: Double,
    val comment: String?
)

data class CreatedReportResponse(
    val id: String,
    val createdAt: String
)