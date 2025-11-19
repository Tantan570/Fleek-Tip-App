package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        //Move to reservation Screen
        val btnReservation = findViewById<Button>(R.id.btnReservation)
        btnReservation.setOnClickListener {
            val intent = Intent(this, ReservationActivity::class.java)
            startActivity(intent)
        }

        //Buttons for opening the link for the socials
        val btnFacebook = findViewById<ImageButton>(R.id.btn_facebook)
        val btnTiktok = findViewById<ImageButton>(R.id.btn_tiktok)

        //Pressing either button will move you to "SocialRedirectActivity.kt" and open the social media page

        // Facebook button
        btnFacebook.setOnClickListener {
            val loadingIntent = Intent(this, SocialRedirectActivity::class.java)
            loadingIntent.putExtra("platform", "facebook")
            startActivity(loadingIntent)
        }

        // TikTok button
        btnTiktok.setOnClickListener {
            val loadingIntent = Intent(this, SocialRedirectActivity::class.java)
            loadingIntent.putExtra("platform", "tiktok")
            startActivity(loadingIntent)
        }

        // Split Button Parts (Nail Art and Eyelash)
        val btnNailArt = findViewById<TextView>(R.id.btnNailArt)
        val btnEyelash = findViewById<TextView>(R.id.btnEyelash)

        // Clicking Nail Art
        btnNailArt.setOnClickListener {
            val intent = Intent(this, NailArtActivity::class.java)
            startActivity(intent)
        }

        // Clicking Eyelash
        btnEyelash.setOnClickListener {
            val intent = Intent(this, EyelashActivity::class.java)
            startActivity(intent)
        }

    }
}