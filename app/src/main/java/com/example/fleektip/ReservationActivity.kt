package com.example.fleektip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ReservationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val btnNailArt = findViewById<Button>(R.id.btnNailArt)
        val btnEyelash = findViewById<Button>(R.id.btnEyelash)
        val btnBoth = findViewById<Button>(R.id.btnBoth)

        var selectedService = ""
        var selectedPrice = 0
        val servicePrices = mapOf(
            "Nail Art" to 350,
            "Eyelash Extension" to 300,
            "Both" to 650
        )

        fun resetButtons() {
            btnNailArt.isSelected = false
            btnEyelash.isSelected = false
            btnBoth.isSelected = false
        }

        btnNailArt.setOnClickListener {
            resetButtons()
            btnNailArt.isSelected = true
            selectedService = "Nail Art"
            selectedPrice = servicePrices[selectedService] ?: 0
        }

        btnEyelash.setOnClickListener {
            resetButtons()
            btnEyelash.isSelected = true
            selectedService = "Eyelash Extension"
            selectedPrice = servicePrices[selectedService] ?: 0
        }

        btnBoth.setOnClickListener {
            resetButtons()
            btnBoth.isSelected = true
            selectedService = "Both"
            selectedPrice = servicePrices[selectedService] ?: 0
        }

        // Date Picker, Converts to YYYY-MM-DD for backend
        val layoutDate = findViewById<LinearLayout>(R.id.layoutDate)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        layoutDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val months = arrayOf(
                        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                    )
                    tvDate.text = "${months[selectedMonth]} $selectedDay, $selectedYear"
                    tvDate.tag = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                },
                year, month, day
            )
            datePicker.show()
        }

        // Time Picker
        val layoutTime = findViewById<LinearLayout>(R.id.layoutTime)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        layoutTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Display in AM/PM
                    val amPm = if (selectedHour >= 12) "PM" else "AM"
                    val hourFormatted = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val minuteFormatted = String.format("%02d", selectedMinute)
                    tvTime.text = "$hourFormatted:$minuteFormatted $amPm"

                    // Converts to 24-hour format for backend
                    val backendHour = String.format("%02d", selectedHour)
                    tvTime.tag = "$backendHour:$minuteFormatted:00"
                },
                hour, minute, false
            )
            timePicker.show()
        }

        val btnCreate = findViewById<Button>(R.id.btnCreateReservation)
        btnCreate.setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            val phone = findViewById<EditText>(R.id.etPhone).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val date = tvDate.tag as? String ?: ""
            val timeApi = tvTime.tag as? String ?: ""
            val timeDisplay = tvTime.text.toString()

            if (name.isBlank() || phone.isBlank() || date.isBlank() || timeApi.isBlank() || selectedService.isBlank()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ReservationConfirmationActivity::class.java).apply {
                putExtra("name", name)
                putExtra("phone", phone)
                putExtra("email", email)
                putExtra("service", selectedService)
                putExtra("price", selectedPrice)
                putExtra("date_display", tvDate.text.toString())
                putExtra("date_api", date)
                putExtra("time_display", timeDisplay)
                putExtra("time_api", timeApi)
            }
            startActivity(intent)
        }
    }
}
