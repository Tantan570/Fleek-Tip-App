package com.example.fleektip

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ToggleButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EyelashActivity : AppCompatActivity() {

    private var selectedLength: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fixed this line
        setContentView(R.layout.ar_screen_eyelash)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val toggleShort = findViewById<ToggleButton>(R.id.toggleShort)
        val toggleMedium = findViewById<ToggleButton>(R.id.toggleMedium)
        val toggleLong = findViewById<ToggleButton>(R.id.toggleLong)

        // Back button functionality
        btnBack.setOnClickListener {
            finish()
        }

        // Toggle buttons logic
        fun clearOtherToggles(except: ToggleButton) {
            val toggles = listOf(toggleShort, toggleMedium, toggleLong)
            for (t in toggles) {
                if (t != except) t.isChecked = false
            }
        }

        toggleShort.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedLength = "Short"
                clearOtherToggles(toggleShort)
                Toast.makeText(this, "Selected: Short", Toast.LENGTH_SHORT).show()
                // TApply short eyelash filter here
            } else if (selectedLength == "Short") {
                selectedLength = null
            }
        }

        toggleMedium.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedLength = "Medium"
                clearOtherToggles(toggleMedium)
                Toast.makeText(this, "Selected: Medium", Toast.LENGTH_SHORT).show()
                // Apply medium eyelash filter here
            } else if (selectedLength == "Medium") {
                selectedLength = null
            }
        }

        toggleLong.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedLength = "Long"
                clearOtherToggles(toggleLong)
                Toast.makeText(this, "Selected: Long", Toast.LENGTH_SHORT).show()
                // Apply long eyelash filter here
            } else if (selectedLength == "Long") {
                selectedLength = null
            }
        }
    }
}
