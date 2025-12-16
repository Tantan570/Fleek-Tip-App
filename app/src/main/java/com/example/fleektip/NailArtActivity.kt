package com.example.fleektip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.PixelCopy
import android.view.ViewStub
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.snap.camerakit.Session
import com.snap.camerakit.invoke
import com.snap.camerakit.lenses.LensesComponent
import com.snap.camerakit.lenses.whenHasFirst
import com.snap.camerakit.support.camerax.CameraXImageProcessorSource
import com.snap.camerakit.supported
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.media.MediaScannerConnection

class NailArtActivity : AppCompatActivity(R.layout.ar_screen_nail) {

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource

    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
        const val COLOR_PICKER_REQUEST = 1001
    }

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startPreview()
            else Log.e("CameraKit", "Camera permission denied.")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Back button
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, ArSelectActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Open Color Picker screen
        findViewById<Button>(R.id.btnSelectColor).setOnClickListener {
            val intent = Intent(this, NailArtPickerActivity::class.java)
            startActivityForResult(intent, COLOR_PICKER_REQUEST)
        }

        // Screenshot button
        findViewById<ImageButton>(R.id.btnScreenshot).setOnClickListener {
            takeScreenshot()
        }

        // Check CameraKit support
        if (!supported(this)) {
            Toast.makeText(this, "CameraKit not supported on this device.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        imageProcessorSource = CameraXImageProcessorSource(
            context = this,
            lifecycleOwner = this
        )

        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startPreview()
        else requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        val LENS_ID = intent.getStringExtra("PUSH_LENS").toString()

        // Attach CameraKit session to ViewStub
        val cameraStub = findViewById<ViewStub>(R.id.camera_kit_stub)
        cameraKitSession = Session(context = this) {
            imageProcessorSource(imageProcessorSource)
            attachTo(cameraStub)
        }

        // Apply requested AR Lens
        cameraKitSession.lenses.repository.observe(
            LensesComponent.Repository.QueryCriteria.ById(LENS_ID, LENS_GROUP_ID)
        ) { result ->
            result.whenHasFirst { lens ->
                cameraKitSession.lenses.processor.apply(lens)
            }
        }
    }

    private fun startPreview() {
        imageProcessorSource.startPreview(false)
    }

    // -------------------------------------------------
    // Screenshot (captures full window including CameraKit)
    // -------------------------------------------------
    private fun takeScreenshot() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(this, "Screenshot requires Android 8.0+", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(window.decorView.width, window.decorView.height, Bitmap.Config.ARGB_8888)

        try {
            PixelCopy.request(
                window,
                bitmap,
                PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        saveToGallery(bitmap)
                        Toast.makeText(this, "Screenshot saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to capture screenshot.", Toast.LENGTH_SHORT).show()
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Error capturing screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Save bitmap to dedicated folder and refresh gallery
    private fun saveToGallery(bitmap: Bitmap) {
        try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val folder = File(picturesDir, "FleekTip")
            if (!folder.exists()) folder.mkdirs()

            val fileName = "AR_Screenshot_${System.currentTimeMillis()}.jpg"
            val file = File(folder, fileName)

            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            outputStream.flush()
            outputStream.close()

            // Make it visible in gallery immediately
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )

            Toast.makeText(this, "Screenshot saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Receive color picker result
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

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }
}
