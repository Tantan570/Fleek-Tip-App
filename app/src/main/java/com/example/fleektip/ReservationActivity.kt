package com.example.fleektip

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fleektip.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class ReservationActivity : AppCompatActivity() {

    private lateinit var spinnerTime: Spinner
    private lateinit var tvDate: TextView
    private var selectedService = ""
    private var selectedPrice = 0
    private val servicePrices = mapOf(
        "Nail Art" to 350,
        "Eyelash Extension" to 300,
        "Both" to 650
    )

    private var availableTimes: List<String> = emptyList() // Store available times for selected date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNailArt = findViewById<Button>(R.id.btnNailArt)
        val btnEyelash = findViewById<Button>(R.id.btnEyelash)
        val btnBoth = findViewById<Button>(R.id.btnBoth)
        val btnCreate = findViewById<Button>(R.id.btnCreateReservation)

        tvDate = findViewById(R.id.tvDate)
        spinnerTime = findViewById(R.id.spinnerTime)

        // Show initial hint in spinner
        setSpinnerHint("Please select a date first")

        // Service selection
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

        // Date picker
        val layoutDate = findViewById<LinearLayout>(R.id.layoutDate)
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

                    // Fetch available times after selecting date
                    fetchAvailableTimes(tvDate.tag as String)
                },
                year, month, day
            )
            datePicker.show()
        }

        // Create reservation
        btnCreate.setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString().trim()
            val phone = findViewById<EditText>(R.id.etPhone).text.toString().trim()
            val dateApi = tvDate.tag as? String ?: ""
            val timeSelected = spinnerTime.selectedItem as? String ?: ""

            if (name.isBlank() || phone.isBlank() || dateApi.isBlank() || timeSelected.isBlank() || selectedService.isBlank()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert selected time to 24-hour format for backend
            val time24 = convertTo24Hour(timeSelected)

            // Check if selected time is still available in list
            if (!availableTimes.contains(timeSelected)) {
                Toast.makeText(this, "Selected time is no longer available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check with backend if slot is still free
            lifecycleScope.launch {
                val available = isSlotAvailable(dateApi, time24)
                if (!available) {
                    Toast.makeText(this@ReservationActivity, "Selected slot is already booked", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Proceed to confirmation activity
                val intent = Intent(this@ReservationActivity, ReservationConfirmationActivity::class.java).apply {
                    putExtra("name", name)
                    putExtra("phone", phone)
                    putExtra("service", selectedService)
                    putExtra("price", selectedPrice)
                    putExtra("date_display", tvDate.text.toString())
                    putExtra("date_api", dateApi)
                    putExtra("time_display", timeSelected) // 12-hour format for UI
                    putExtra("time_api", time24) // 24-hour format for backend
                }
                startActivity(intent)
            }
        }

        // Back button
        btnBack.setOnClickListener { finish() }
    }

    // Fetch available times from PHP backend
    private fun fetchAvailableTimes(date: String) {
        lifecycleScope.launch {
            try {
                val client = OkHttpClient()
                val encodedDate = URLEncoder.encode(date, "UTF-8")
                val url = "${RetrofitClient.getBaseUrl()}get_available_times.php?date=$encodedDate"
                val request = Request.Builder().url(url).build()
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                val responseText = response.body?.string() ?: ""
                val json = JSONObject(responseText)

                if (json.getBoolean("success")) {
                    val times = json.getJSONArray("times")
                    val list = mutableListOf<String>()
                    for (i in 0 until times.length()) {
                        list.add(times.getString(i))
                    }

                    availableTimes = list // store fetched times

                    val adapter = ArrayAdapter(
                        this@ReservationActivity,
                        android.R.layout.simple_spinner_item,
                        availableTimes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTime.adapter = adapter
                } else {
                    Toast.makeText(this@ReservationActivity, "No available times", Toast.LENGTH_SHORT).show()
                    setSpinnerHint("No available times")
                    availableTimes = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ReservationActivity, "Error fetching times", Toast.LENGTH_SHORT).show()
                setSpinnerHint("Error fetching times")
                availableTimes = emptyList()
            }
        }
    }

    // Helper to show a hint in spinner
    private fun setSpinnerHint(hint: String) {
        val adapter = ArrayAdapter(
            this@ReservationActivity,
            android.R.layout.simple_spinner_item,
            listOf(hint)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTime.adapter = adapter
    }

    // Backend check if slot is available
    private suspend fun isSlotAvailable(date: String, time24: String): Boolean {
        return try {
            val client = OkHttpClient()
            val encodedDate = URLEncoder.encode(date, "UTF-8")
            val encodedTime = URLEncoder.encode(time24, "UTF-8")
            val url = "${RetrofitClient.getBaseUrl()}check_slot.php?date=$encodedDate&time=$encodedTime"
            val request = Request.Builder().url(url).build()
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            val responseText = response.body?.string() ?: ""
            val json = JSONObject(responseText)
            json.getBoolean("available")
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Convert 12-hour time (AM/PM) to 24-hour format (For Checking)
    private fun convertTo24Hour(time12: String): String {
        return try {
            val sdf12 = SimpleDateFormat("h:mm a", Locale.US)
            val sdf24 = SimpleDateFormat("HH:mm:ss", Locale.US)
            val date = sdf12.parse(time12)
            sdf24.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            "00:00:00"
        }
    }
}
