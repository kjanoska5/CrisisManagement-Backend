package com.example.crisismanagement.app.common.network

data class ReportSummary(
    val id: String,
    val lat: Double,
    val lng: Double,
    val status: ReportStatus?,
    val threatTypeId: String,
    val comment: String?
)
