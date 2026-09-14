package com.example.crisismanagement.app.common.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZgloszeniaApi {

    @GET("api/typy-zagrozen")
    suspend fun pobierzTypyZagrozen(): List<TypZagrozenia>

    @POST("api/zgloszenia")
    suspend fun wyslijZgloszenie(@Body request: NoweZgloszenieRequest): ZgloszenieResponse
}