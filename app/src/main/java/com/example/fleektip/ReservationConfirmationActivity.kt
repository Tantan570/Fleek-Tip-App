package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReservationConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_confirmation)

        // Retrieve passed data from previous activity
        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val service = intent.getStringExtra("service")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")

        // Bind TextViews and display data
        findViewById<TextView>(R.id.tvConfirmName).text = name
        findViewById<TextView>(R.id.tvConfirmPhone).text = phone
        findViewById<TextView>(R.id.tvConfirmService).text = service
        findViewById<TextView>(R.id.tvConfirmDate).text = date
        findViewById<TextView>(R.id.tvConfirmTime).text = time

        // Confirm button — finalize reservation
        findViewById<Button>(R.id.btnConfirmReservation).setOnClickListener {
            // Add logic for API call for saving to database
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // "Edit" button — return to the previous reservation form
        findViewById<Button>(R.id.btnEditReservation).setOnClickListener {
            finish()
        }
    }
}
