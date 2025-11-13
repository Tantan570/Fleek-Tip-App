package com.example.fleektip.network

import com.example.fleektip.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ⚠️ Replace this with your actual server URL or local IP address
    // Example (if you use XAMPP on local network): "http://192.168.1.10/salon_api/"
    // Example (if using localhost for emulator): "http://10.0.2.2/salon_api/"
    private const val BASE_URL = "http://10.0.2.2/salon_api/"

    // Lazy initialization — Retrofit instance is created only when first used
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // for JSON parsing
            .build()
            .create(ApiService::class.java)
    }
}
