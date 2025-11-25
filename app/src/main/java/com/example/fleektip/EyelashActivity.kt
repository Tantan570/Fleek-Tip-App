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
            if (isGranted) {
                startPreview()
            } else {
                Log.e("CameraKit", "Camera permission denied by user.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageProcessorSource = CameraXImageProcessorSource(
            context = this, lifecycleOwner = this
        )

        // If camera permission is granted, then start the preview
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startPreview()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val getLensEyelash = intent.getStringExtra("PUSH_LENS")
        val LENS_ID = getLensEyelash.toString()

        cameraKitSession = Session(context = this) {
            imageProcessorSource(imageProcessorSource)
            attachTo(findViewById(R.id.camera_kit_stub_eyelash))
        }.apply {
            lenses.repository.observe(
                LensesComponent.Repository.QueryCriteria.ById(LENS_ID, LENS_GROUP_ID)
            ) { result ->
                result.whenHasFirst { requestedLens ->
                    lenses.processor.apply(requestedLens)
                }
            }
        }

        // Back button
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, ArSelectActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
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
                "Classic" to "Light" -> "2005eceb-31fd-4fc6-8b6f-b2495e48cc38" // add lens ID
                "Classic" to "Medium" -> "c761acd6-d5ff-4ce0-bc25-d4209a3d54a8"
                "Classic" to "Heavy" -> "c0b51dfe-7e83-4c02-bdc9-8438ec08602c"
                "Cat Eyes" to "Light" -> "a5becf40-5103-411c-af8c-ff1d04525820"
                "Cat Eyes" to "Medium" -> "9310f8a7-95d5-42a3-94e6-d8c5c8ec6bd2"
                "Cat Eyes" to "Heavy" -> "386056c7-7733-40f7-a9b2-2dc27f83d1eb"
                "Doll Eyes" to "Light" -> "c7469aa2-511d-410d-b4f4-66f7d1d8076a"
                "Doll Eyes" to "Medium" -> "642244c6-9065-40bd-a57c-2f8db0dbed6b"
                "Doll Eyes" to "Heavy" -> "72c5c4d5-4110-441f-9dce-8f7b1e322ba1"
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
    private fun startPreview() {
        imageProcessorSource.startPreview(true)
    }
    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }
}
