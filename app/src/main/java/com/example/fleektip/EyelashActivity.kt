package com.example.fleektip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.snap.camerakit.Session
import com.snap.camerakit.invoke
import com.snap.camerakit.lenses.LensesComponent
import com.snap.camerakit.lenses.whenHasFirst
import com.snap.camerakit.support.camerax.CameraXImageProcessorSource
import com.snap.camerakit.supported
import android.widget.ToggleButton

class EyelashActivity : AppCompatActivity(R.layout.ar_screen_eyelash) {

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource


    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
    }

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startPreview()
            } else {
                Log.e("CameraKit", "Camera permission denied by user.")
            }
        }

    private var selectedLength: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageProcessorSource = CameraXImageProcessorSource(
            context = this, lifecycleOwner = this
        )

        // Start preview if permission granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startPreview()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val getLensEyelash = intent.getStringExtra("PUSH_LENS")
        val LENS_ID = getLensEyelash.toString()

        // Initialize CameraKit session
        cameraKitSession = Session(context = this) {
            imageProcessorSource(imageProcessorSource)
            attachTo(findViewById(R.id.camera_kit_stub_eyelash))
        }
            .apply {
                lenses.repository.observe(
                    LensesComponent.Repository.QueryCriteria.ById(LENS_ID,
                        NailArtActivity.Companion.LENS_GROUP_ID
                    )
                ) { result ->
                    result.whenHasFirst { requestedLens ->
                        lenses.processor.apply(requestedLens)
                    }
                }
            }

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
                val intent = Intent(this, EyelashActivity::class.java)
                intent.putExtra("PUSH_LENS", "2005eceb-31fd-4fc6-8b6f-b2495e48cc38")
                startActivity(intent)

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
                val intent = Intent(this, EyelashActivity::class.java)
                intent.putExtra("PUSH_LENS", "c761acd6-d5ff-4ce0-bc25-d4209a3d54a8")
                startActivity(intent)

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
                val intent = Intent(this, EyelashActivity::class.java)
                intent.putExtra("PUSH_LENS", "c0b51dfe-7e83-4c02-bdc9-8438ec08602c")
                startActivity(intent)

            } else if (selectedLength == "Long") {
                selectedLength = null
            }
        }
    }

    private fun startPreview() {
        imageProcessorSource.startPreview(true)
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }

}
