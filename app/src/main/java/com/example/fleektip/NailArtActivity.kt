package com.example.fleektip
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import android.util.Log
import android.widget.ImageButton
class NailArtActivity : AppCompatActivity(R.layout.ar_screen_nail){

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource

    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
        const val LENS_ID = "2e3e6bf7-8231-4ed2-b476-32302d14a520"
    }

    // Initialize a permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startPreview()
            } else {
                // Explain to the user that Camera Kit is unavailable because the
                Log.e("CameraKit", "Requested camera permission is denied by the user, then attempt retry.");
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // closes the current AR activity and returns to the previous screen
        }



        // Checking if Camera Kit is supported on this device or not.
        if (!supported(this)) {
            Log.e("CameraKit", "Device is not supported");
            finish()
            return
        }

        imageProcessorSource = CameraXImageProcessorSource(
            context = this, lifecycleOwner = this
        )

        // If camera permission is granted, then start the preview
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startPreview()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

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
        // starting preview with world facing camera
        imageProcessorSource.startPreview(false)
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }
}