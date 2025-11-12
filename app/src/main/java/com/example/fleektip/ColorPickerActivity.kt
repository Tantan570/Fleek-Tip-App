package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ColorPickerActivity : AppCompatActivity() {

    private var selectedSet: String? = null // "A" or "B"
    private var selectedNailLength: String? = null // "short", "medium", "long"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.color_picker)

        // Set buttons
        val btnSetA = findViewById<Button>(R.id.btnSetA)
        val btnSetB = findViewById<Button>(R.id.btnSetB)

        btnSetA.setOnClickListener {
            selectedSet = "A"
            btnSetA.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnSetB.setBackgroundTintList(getColorStateList(android.R.color.white))
            Toast.makeText(this, "Set A selected", Toast.LENGTH_SHORT).show()
        }

        btnSetB.setOnClickListener {
            selectedSet = "B"
            btnSetB.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnSetA.setBackgroundTintList(getColorStateList(android.R.color.white))
            Toast.makeText(this, "Set B selected", Toast.LENGTH_SHORT).show()
        }

        // Nail length buttons
        val btnShort = findViewById<Button>(R.id.btnNailShort)
        val btnMedium = findViewById<Button>(R.id.btnNailMedium)
        val btnLong = findViewById<Button>(R.id.btnNailLong)

        btnShort.setOnClickListener {
            selectedNailLength = "short"
            btnShort.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnMedium.setBackgroundTintList(getColorStateList(android.R.color.white))
            btnLong.setBackgroundTintList(getColorStateList(android.R.color.white))
        }

        btnMedium.setOnClickListener {
            selectedNailLength = "medium"
            btnMedium.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnShort.setBackgroundTintList(getColorStateList(android.R.color.white))
            btnLong.setBackgroundTintList(getColorStateList(android.R.color.white))
        }

        btnLong.setOnClickListener {
            selectedNailLength = "long"
            btnLong.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnShort.setBackgroundTintList(getColorStateList(android.R.color.white))
            btnMedium.setBackgroundTintList(getColorStateList(android.R.color.white))
        }

        // Color buttons
        val colors = mapOf(
            R.id.btnColorRed to "red",
            R.id.btnColorBlue to "blue",
            R.id.btnColorWhite to "white",
            R.id.btnColorBrown to "brown",
            R.id.btnColorPink to "pink"
        )

        for ((id, colorName) in colors) {
            findViewById<Button>(id).setOnClickListener {
                if (selectedSet == null) {
                    Toast.makeText(this, "Please select a Set first.", Toast.LENGTH_SHORT).show()
                } else if (selectedNailLength == null) {
                    Toast.makeText(this, "Please select a Nail Length first.", Toast.LENGTH_SHORT).show()
                } else {
                    val resultIntent = Intent().apply {
                        putExtra("selectedColor", colorName)
                        putExtra("setType", selectedSet)
                        putExtra("nailLength", selectedNailLength)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}
