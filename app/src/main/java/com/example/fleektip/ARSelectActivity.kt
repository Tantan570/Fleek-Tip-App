package com.example.fleektip
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton

    class ArSelectActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.select_ar_screen)

            val btnBack = findViewById<ImageButton>(R.id.btnBack)
            btnBack.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            // AR Options
            val btnEyelash = findViewById<Button>(R.id.btnEyelashAR)
            val btnNailArt = findViewById<Button>(R.id.btnNailArtAR)

            btnEyelash.setOnClickListener {
                val intent = Intent(this, EyelashActivity::class.java)
                startActivity(intent)
            }

            btnNailArt.setOnClickListener {
                val intent = Intent(this, NailArtActivity::class.java)
                startActivity(intent)
            }
        }
    }