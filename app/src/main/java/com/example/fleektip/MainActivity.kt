package com.example.fleektip
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var myWebView: WebView
    private lateinit var tryOnButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        myWebView = findViewById(R.id.webview)

        val webSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true

        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()

        myWebView.loadUrl("https://mjluscious.online/")

        // Back button behavior
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (myWebView.canGoBack()) {
                    myWebView.goBack()
                } else {
                    finish()
                }
            }
        })

        //Try-On Floating Button
        tryOnButton = findViewById(R.id.tryOnButton)
        tryOnButton.setOnClickListener {
            val intent = Intent(this, ArSelectActivity::class.java)
            startActivity(intent)
        }
    }
}
