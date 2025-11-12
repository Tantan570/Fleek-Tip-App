package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ColorPickerActivity : AppCompatActivity() {

    private var selectedSet: String? = null // "A" or "B"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.color_picker)

        val btnSetA = findViewById<Button>(R.id.btnSetA)
        val btnSetB = findViewById<Button>(R.id.btnSetB)

        // Set selection
        btnSetA.setOnClickListener {
            selectedSet = "A"
            btnSetA.setBackgroundTintList(getColorStateList(R.color.teal_200))
            btnSetB.setBackgroundTintList(getColorStateList(android.R.color.white))
            Toast.makeText(this, "Set A selected", Toast.LENGTH_SHORT).show()
        }

        btnSetB.setOnClickListener {
            selectedSet = "B"
            btnSetB.setBackgroundTintList(getColorStateList(R.color.teal_200))
            btnSetA.setBackgroundTintList(getColorStateList(android.R.color.white))
            Toast.makeText(this, "Set B selected", Toast.LENGTH_SHORT).show()
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
                } else {
                    val resultIntent = Intent()
                    resultIntent.putExtra("selectedColor", colorName)
                    resultIntent.putExtra("setType", selectedSet)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}
