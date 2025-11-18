package com.example.fleektip.network

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("create_reservation.php")
    suspend fun insertBooking(@Body request: ReservationRequest): ReservationResponse
}
