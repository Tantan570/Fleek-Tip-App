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

class NailArtActivity : AppCompatActivity(R.layout.ar_screen_nail) {

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource

    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
        const val LENS_ID = "3d64a29b-2431-4904-a8b6-a8c8243786c0"
        // Request code for color picker
        const val COLOR_PICKER_REQUEST = 1001
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Button Blue
        findViewById<ImageButton>(R.id.btnSelectColor).setOnClickListener {
            val LENS_POLISH_BLUE = "121f091e-7010-43b6-8700-2fe34a9b11e5"
            if (LENS_ID != LENS_POLISH_BLUE){
                cameraKitSession.lenses.processor.clear()
                cameraKitSession = Session(context = this) {
                    imageProcessorSource(imageProcessorSource)
                    attachTo(findViewById(R.id.camera_kit_stub))
                }.apply {
                    lenses.repository.observe(
                        LensesComponent.Repository.QueryCriteria.ById(LENS_POLISH_BLUE, LENS_GROUP_ID)
                    ) { result ->
                        result.whenHasFirst { requestedLens ->
                            lenses.processor.apply(requestedLens)
                        }
                    }
                }
            }
        }

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Open Color Picker screen
        findViewById<ImageButton>(R.id.btnSelectColor).setOnClickListener {
            val intent = Intent(this, ColorPickerActivity::class.java)
            startActivityForResult(intent, COLOR_PICKER_REQUEST)
        }

        // Check if Camera Kit is supported
        if (!supported(this)) {
            Log.e("CameraKit", "Device not supported for CameraKit.")
            finish()
            return
        }

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

        // Initialize CameraKit session
        cameraKitSession = Session(context = this) {
            imageProcessorSource(imageProcessorSource)
            attachTo(findViewById(R.id.camera_kit_stub))
        }.apply {
            lenses.repository.observe(
                LensesComponent.Repository.QueryCriteria.ById(LENS_ID, LENS_GROUP_ID)
            ) { result ->
                result.whenHasFirst { requestedLens ->
                    lenses.processor.apply(requestedLens)
                }
            }
        }
    }

    private fun startPreview() {
        imageProcessorSource.startPreview(false)
    }

    // Receive the selected color and nail length from ColorPickerActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == COLOR_PICKER_REQUEST && resultCode == RESULT_OK) {
            val selectedColor = data?.getStringExtra("selectedColor")
            val selectedSet = data?.getStringExtra("setType")
            val selectedNailLength = data?.getStringExtra("nailLength")

            val msg = buildString {
                append("Color: ${selectedColor ?: "none"}\n")
                append("Set: ${selectedSet ?: "none"}\n")
                append("Nail Length: ${selectedNailLength ?: "none"}")
            }

            // You can implement your overlay logic here
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }
}
