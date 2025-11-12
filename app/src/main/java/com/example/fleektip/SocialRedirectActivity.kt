package com.example.fleektip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SocialRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_redirect)

        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val platform = intent.getStringExtra("platform") ?: ""

        // Set the appropriate message
        tvMessage.text = when (platform) {
            "facebook" -> "Opening Facebook..."
            "tiktok" -> "Opening TikTok..."
            else -> "Opening..."
        }

        // Delay for smoother transition
        Handler(Looper.getMainLooper()).postDelayed({
            when (platform) {
                "facebook" -> openFacebook()
                "tiktok" -> openTikTok()
            }
            finish()
        }, 1200) // ~1.2 seconds
    }

    //Try to open facebook app first if installed then revert to opening the facebook page link in the browser if not.
    private fun openFacebook() {
        try {
            val fbIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/mjluscious"))
            startActivity(fbIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/mjluscious"))
            startActivity(webIntent)
        }
    }

    //Try to open Tiktok app first ir installed then revert to opening the Tiktok page link on the browser if not.
    private fun openTikTok() {
        try {
            val appIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.tiktok.com/@mjluscious"))
            appIntent.setPackage("com.zhiliaoapp.musically")
            startActivity(appIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.tiktok.com/@mjluscious"))
            startActivity(webIntent)
        }
    }
}