package com.example.crisismanagement.app.common.network

import com.example.crisismanagement.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    val api: ReportsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportsApi::class.java)
    }
}
