package com.example.crisismanagement.app.common.network

import com.example.app.common.network.MedicalCardRequest
import com.example.app.common.network.MedicalCardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface ZgloszeniaApi {


    @GET(value = "api/threat-types")
    suspend fun pobierzTypyZagrozen(): List<TypZagrozenia>

    @POST(value = "api/reports")
    suspend fun wyslijZgloszenie(@Body request: NoweZgloszenieRequest)

    // pobieranie karty medycznej
    @GET(value = "api/users/{userId}/medical-card")
    suspend fun getMedicalCard(@Path("userId") userId: UUID): Response<MedicalCardResponse>

    // aktualizacja karty medycznej
    @PUT(value = "api/users/{userId}/medical-card")
    suspend fun updateMedicalCard(
        @Path("userId") userId: UUID,
        @Body request: MedicalCardRequest
    ): Response<MedicalCardResponse>
}