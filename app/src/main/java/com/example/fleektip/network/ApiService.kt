package com.example.fleektip.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import com.example.fleektip.model.ApiResponse
import com.example.fleektip.model.BookingResponse


interface ApiService {
    @FormUrlEncoded
    @POST("insert_booking.php")
    suspend fun insertBooking(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("service_type") serviceType: String,
        @Field("date") date: String,
        @Field("time_slot") timeSlot: String,
        @Field("price") price: Double
    ): ApiResponse

    @GET("get_bookings.php")
    suspend fun getBookings(): BookingResponse
}