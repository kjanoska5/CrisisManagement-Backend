package com.example.crisismanagement.app.common.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ZgloszeniaApi {


    @GET(value = "api/threat-types")
    suspend fun pobierzTypyZagrozen(): List<ThreatType>

    @POST(value = "api/reports")
    suspend fun wyslijZgloszenie(@Body request: NewReportRequest)

    // pobieranie karty medycznej
    @GET("api/users/{userId}/medical-card")
    suspend fun getMedicalCard(@Path("userId") userId: String): Response<MedicalCardResponse>

    // aktualizacja karty medycznej
    @PUT("api/users/{userId}/medical-card")
    suspend fun updateMedicalCard(
        @Path("userId") userId: String,
        @Body request: MedicalCardRequest
    ): Response<MedicalCardResponse>
}