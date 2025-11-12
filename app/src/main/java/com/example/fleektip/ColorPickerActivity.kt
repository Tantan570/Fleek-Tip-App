package com.example.fleektip

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity

class ColorPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.color_picker)

        val gridLayout = findViewById<GridLayout>(R.id.gridColors)

        // ⚡ Easy-to-update list of colors (currently all white)
        val colors = listOf(
            "#FFFFFF", // White
            "#FFFFFF", // White
            "#FFFFFF", // White
            "#FFFFFF", // White
            "#FFFFFF"  // White
        )

        colors.forEach { colorHex ->
            val colorInt = Color.parseColor(colorHex)

            val colorButton = Button(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 150
                    height = 150
                    setMargins(16, 16, 16, 16)
                }
                setBackgroundColor(colorInt)
                setOnClickListener {
                    val resultIntent = Intent()
                    resultIntent.putExtra("selectedColor", colorHex)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
            gridLayout.addView(colorButton)
        }
    }
}
