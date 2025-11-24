package com.example.fleektip

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.PixelCopy
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.media.MediaScannerConnection
import android.os.Environment

class EyelashActivity : AppCompatActivity(R.layout.ar_screen_eyelash) {

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource

    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
        const val EYE_LASH_PICKER_REQUEST = 2001
    }

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startPreview()
            else Log.e("CameraKit", "Camera permission denied by user.")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageProcessorSource = CameraXImageProcessorSource(context = this, lifecycleOwner = this)

        // Start preview if permission granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) startPreview()
        else requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        val LENS_ID = intent.getStringExtra("PUSH_LENS") ?: ""

        // Initialize CameraKit session
        cameraKitSession = Session(context = this) {
            imageProcessorSource(imageProcessorSource)
            attachTo(findViewById(R.id.camera_kit_stub))
        }.apply {
            if (LENS_ID.isNotEmpty()) {
                lenses.repository.observe(
                    LensesComponent.Repository.QueryCriteria.ById(LENS_ID, LENS_GROUP_ID)
                ) { result ->
                    result.whenHasFirst { requestedLens ->
                        lenses.processor.apply(requestedLens)
                    }
                }
            }
        }

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
            startActivity(Intent(this, ArSelectActivity::class.java))
        }

        // Screenshot button
        findViewById<ImageButton>(R.id.btnScreenshot).setOnClickListener {
            takeScreenshot()
        }

        // Open Eyelash Picker
        findViewById<ImageButton?>(R.id.btnSelectFilter)?.setOnClickListener {
            val intent = Intent(this, EyelashPickerActivity::class.java)
            startActivityForResult(intent, EYE_LASH_PICKER_REQUEST)
        }
    }

    private fun startPreview() {
        imageProcessorSource.startPreview(true)
    }

    // --------------------------
    // Screenshot Logic
    // --------------------------
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

    private fun saveToGallery(bitmap: Bitmap) {
        try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val folder = File(picturesDir, "FleekTip")
            if (!folder.exists()) folder.mkdirs()

            val fileName = "AR_Eyelash_Screenshot_${System.currentTimeMillis()}.jpg"
            val file = File(folder, fileName)

            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            outputStream.flush()
            outputStream.close()

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

    // Receive Eyelash Picker result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EYE_LASH_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val style = data?.getStringExtra("selectedStyle")
            val thickness = data?.getStringExtra("selectedThickness")

            val lensId = when (style to thickness) {
                "Classic" to "Light" -> "" // add lens ID
                "Classic" to "Medium" -> ""
                "Classic" to "Heavy" -> ""
                "Cat Eyes" to "Light" -> ""
                "Cat Eyes" to "Medium" -> ""
                "Cat Eyes" to "Heavy" -> ""
                "Doll Eyes" to "Light" -> ""
                "Doll Eyes" to "Medium" -> ""
                "Doll Eyes" to "Heavy" -> ""
                else -> null
            }

            lensId?.let {
                cameraKitSession.lenses.repository.observe(
                    LensesComponent.Repository.QueryCriteria.ById(it, LENS_GROUP_ID)
                ) { result ->
                    result.whenHasFirst { lens ->
                        cameraKitSession.lenses.processor.apply(lens)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }
}
