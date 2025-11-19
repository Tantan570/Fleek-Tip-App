package com.example.fleektip.network

import TimeResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query



interface ApiService {
    @POST("create_reservation.php")
    suspend fun insertBooking(@Body request: ReservationRequest): ReservationResponse

    @GET("get_available_times.php")
    suspend fun getAvailableTimes(@Query("date") date: String): TimeResponse
}
