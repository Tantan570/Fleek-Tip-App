package com.example.fleektip
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fleektip.network.RetrofitClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ReservationConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_confirmation)

        // Retrieve passed data from previous activity
        val name = intent.getStringExtra("name") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val service = intent.getStringExtra("service") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val price = intent.getIntExtra("price", 0).toDouble()
        val email = intent.getStringExtra("email") ?: ""


        // Bind TextViews and display data
        findViewById<TextView>(R.id.tvConfirmName).text = name
        findViewById<TextView>(R.id.tvConfirmPhone).text = phone
        findViewById<TextView>(R.id.tvConfirmEmail).text = email
        findViewById<TextView>(R.id.tvConfirmService).text = service
        findViewById<TextView>(R.id.tvConfirmDate).text = date
        findViewById<TextView>(R.id.tvConfirmTime).text = time
        findViewById<TextView>(R.id.tvConfirmPrice).text = "₱$price"

        val btnConfirm = findViewById<Button>(R.id.btnConfirmReservation)
        val btnEdit = findViewById<Button>(R.id.btnEditReservation)

        // Confirm reservation button
        btnConfirm.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.insertBooking(
                        name = name,
                        phone = phone,
                        email = email,
                        serviceType = service,
                        date = date,
                        timeSlot = time,
                        price = price
                    )

                    if (response.success) {
                        Toast.makeText(this@ReservationConfirmationActivity, "Reservation confirmed!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ReservationConfirmationActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@ReservationConfirmationActivity, response.message ?: "Failed to confirm reservation", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ReservationConfirmationActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

        // Edit button
        btnEdit.setOnClickListener {
            finish()
        }
    }
}
