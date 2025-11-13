package com.example.fleektip.model

data class Booking(
    val id: Int? = null,
    val name: String,
    val phone: String,
    val service_type: String,
    val date: String,
    val time_slot: String,
    val price: Double,
    val status: String? = null,
    val created_at: String? = null
)
