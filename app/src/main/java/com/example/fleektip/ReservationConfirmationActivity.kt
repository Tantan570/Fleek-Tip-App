package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fleektip.network.ReservationRequest
import com.example.fleektip.network.ReservationResponse
import com.example.fleektip.network.RetrofitClient
import kotlinx.coroutines.launch

class ReservationConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_confirmation)

        val name = intent.getStringExtra("name") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val service = intent.getStringExtra("service") ?: ""
        val price = intent.getIntExtra("price", 0).toDouble()
        val dateDisplay = intent.getStringExtra("date_display") ?: ""
        val dateApi = intent.getStringExtra("date_api") ?: ""
        val timeDisplay = intent.getStringExtra("time_display") ?: ""
        val timeApi = intent.getStringExtra("time_api") ?: ""

        findViewById<TextView>(R.id.tvConfirmName).text = name
        findViewById<TextView>(R.id.tvConfirmPhone).text = phone
        findViewById<TextView>(R.id.tvConfirmService).text = service
        findViewById<TextView>(R.id.tvConfirmDate).text = dateDisplay
        findViewById<TextView>(R.id.tvConfirmTime).text = timeDisplay
        findViewById<TextView>(R.id.tvConfirmPrice).text = "₱$price"

        val btnConfirm = findViewById<Button>(R.id.btnConfirmReservation)
        val btnEdit = findViewById<Button>(R.id.btnEditReservation)

        btnConfirm.setOnClickListener {

            val serviceTypeMapped = when(service) {
                "Nail Art" -> "Nail"
                "Eyelash Extension" -> "Eyelash"
                "Both" -> "Both"
                else -> ""
            }

            if(serviceTypeMapped.isEmpty()) {
                Toast.makeText(this, "Invalid service selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val request = ReservationRequest(
                        name = name,
                        phone = phone,
                        service_type = serviceTypeMapped,
                        date = dateApi,
                        time_slot = timeApi // backend-friendly 24-hour
                    )

                    val response: ReservationResponse = RetrofitClient.api.insertBooking(request)

                    if(response.success) {
                        Toast.makeText(this@ReservationConfirmationActivity, "Reservation confirmed!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ReservationConfirmationActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@ReservationConfirmationActivity, response.error ?: "Failed to confirm reservation", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@ReservationConfirmationActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

        btnEdit.setOnClickListener {
            finish()
        }
    }
}
