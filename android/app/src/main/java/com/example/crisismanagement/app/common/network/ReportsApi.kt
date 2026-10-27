package com.example.crisismanagement.app.common.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReportsApi {

    @GET("api/threat-types")
    suspend fun getThreatTypes(): List<ThreatType>

    @POST("api/reports")
    suspend fun sendReport(@Body request: NewReportRequest)

    @GET("api/users/{userId}/medical-card")
    suspend fun getMedicalCard(@Path("userId") userId: String): Response<MedicalCardResponse>

    @DELETE("api/users/{userId}/medical-card")
    suspend fun deleteMedicalCard(@Path("userId") userId: String): Response<Unit>

    @PUT("api/users/{userId}/medical-card")
    suspend fun updateMedicalCard(
        @Path("userId") userId: String,
        @Body request: MedicalCardRequest
    ): Response<MedicalCardResponse>

    @GET("api/reports")
    suspend fun getReports(): List<ReportSummary>

    @POST("api/reports")
    suspend fun createServiceReport(@Body request: ServiceCreateReportRequest): CreatedReportResponse

    @PUT("api/reports/{id}")
    suspend fun updateReport(
        @Path("id") id: String,
        @Body request: ServiceUpdateReportRequest
    ): ReportSummary

    @DELETE("api/reports/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Unit>
}
