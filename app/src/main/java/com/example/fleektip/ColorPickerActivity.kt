package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.fleektip.NailArtActivity.Companion.COLOR_PICKER_REQUEST
import com.snap.camerakit.Session
import com.snap.camerakit.invoke
import com.snap.camerakit.lenses.LensesComponent
import com.snap.camerakit.lenses.whenHasFirst
import com.snap.camerakit.support.camerax.CameraXImageProcessorSource
import com.snap.camerakit.supported

class ColorPickerActivity : AppCompatActivity(R.layout.ar_screen_nail) {

    private lateinit var cameraKitSession: Session
    private lateinit var imageProcessorSource: CameraXImageProcessorSource
    private var selectedSet: String? = null
    private var selectedNailLength: String? = null
    private var selectedColor: String? = null
    private var isNailPolishMode: Boolean = false

    companion object {
        const val LENS_GROUP_ID = "f183295f-d40e-41d8-a045-860713e44243"
        const val LENS_ID = "2e3e6bf7-8231-4ed2-b476-32302d14a520"
        const val LENS_SET_B ="80bea708-21e6-4698-9c55-24e46eb8ec61"
    }

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
                LensesComponent.Repository.QueryCriteria.ById(
                    NailArtActivity.Companion.LENS_ID,
                    NailArtActivity.Companion.LENS_GROUP_ID
                )
            ) { result ->
                result.whenHasFirst { requestedLens ->
                    lenses.processor.apply(requestedLens)
                }
            }
        }

        setContentView(R.layout.color_picker)

        // --- UI references ---
        val btnSetA = findViewById<Button>(R.id.btnSetA)
        val btnSetB = findViewById<Button>(R.id.btnSetB)
        val btnShort = findViewById<Button>(R.id.btnNailShort)
        val btnMedium = findViewById<Button>(R.id.btnNailMedium)
        val btnLong = findViewById<Button>(R.id.btnNailLong)
        val togglePolish = findViewById<Switch>(R.id.switchNailPolish)

        // --- Nail Polish Toggle ---
        togglePolish.setOnCheckedChangeListener { _, isChecked ->
            isNailPolishMode = isChecked
            if (isChecked) {
                Toast.makeText(this, "Nail Polish mode ON", Toast.LENGTH_SHORT).show()
                //can disable or reset other filters here if needed
            } else {
                Toast.makeText(this, "Nail Polish mode OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Set Buttons (Premade Designs) ---
        btnSetA.setOnClickListener {
            if (isNailPolishMode) {
                Toast.makeText(this, "Turn off Nail Polish mode first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedSet = "A"
            selectedNailLength = null
            selectedColor = null

            btnSetA.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnSetB.setBackgroundTintList(getColorStateList(android.R.color.white))

            Toast.makeText(this, "Set A selected", Toast.LENGTH_SHORT).show()

            //Apply AR filter logic for "Set A" here before returning result
            cameraKitSession.lenses.processor.clear()
            cameraKitSession = Session(context = this) {
                imageProcessorSource(imageProcessorSource)
                attachTo(findViewById(R.id.camera_kit_stub))
            }.apply {
                lenses.repository.observe(
                    LensesComponent.Repository.QueryCriteria.ById(LENS_SET_B, LENS_GROUP_ID)
                ) { result ->
                    result.whenHasFirst { requestedLens ->
                        lenses.processor.apply(requestedLens)
                    }
                }
            }

            returnPremadeDesign("A")
        }

        btnSetB.setOnClickListener {
            if (isNailPolishMode) {
                Toast.makeText(this, "Turn off Nail Polish mode first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedSet = "B"
            selectedNailLength = null
            selectedColor = null

            btnSetB.setBackgroundTintList(getColorStateList(R.color.pink_light))
            btnSetA.setBackgroundTintList(getColorStateList(android.R.color.white))

            Toast.makeText(this, "Set B selected", Toast.LENGTH_SHORT).show()

            //Apply AR filter logic for "Set B" here before returning result
            cameraKitSession.lenses.processor.clear()
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


            returnPremadeDesign("B")
        }

        // --- Nail Length Buttons ---
        btnShort.setOnClickListener {
            if (isNailPolishMode) {
                Toast.makeText(this, "Nail Polish mode active — cannot change length.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedSet = null
            selectedNailLength = "short"
            highlightLengthButtons(btnShort, btnMedium, btnLong)
        }

        btnMedium.setOnClickListener {
            if (isNailPolishMode) {
                Toast.makeText(this, "Nail Polish mode active — cannot change length.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedSet = null
            selectedNailLength = "medium"
            highlightLengthButtons(btnMedium, btnShort, btnLong)
        }

        btnLong.setOnClickListener {
            if (isNailPolishMode) {
                Toast.makeText(this, "Nail Polish mode active — cannot change length.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedSet = null
            selectedNailLength = "long"
            highlightLengthButtons(btnLong, btnShort, btnMedium)
        }

        // --- Color Buttons ---
        val colors = mapOf(
            R.id.btnColorRed to "red",
            R.id.btnColorBlue to "blue",
            R.id.btnColorWhite to "white",
            R.id.btnColorBrown to "brown",
            R.id.btnColorPink to "pink"
        )

        for ((id, colorName) in colors) {
            findViewById<Button>(id).setOnClickListener {
                //Nail Polish Mode
                if (isNailPolishMode) {
                    // Insert Logic for Nail Polish Here

                    val resultIntent = Intent().apply {
                        putExtra("selectedColor", colorName)
                        putExtra("nailPolishMode", true)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    return@setOnClickListener
                }

                // For handling Color + Length
                selectedColor = colorName
                selectedSet = null // clear sets

                if (selectedNailLength == null) {
                    Toast.makeText(this, "Please select a Nail Length.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Insert Logic for Nail Length + Color Here

                val resultIntent = Intent().apply {
                    putExtra("selectedColor", colorName)
                    putExtra("nailLength", selectedNailLength)
                    putExtra("setType", "Custom")
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun startPreview() {
        imageProcessorSource.startPreview(false)
    }

    override fun onDestroy() {
        cameraKitSession.close()
        super.onDestroy()
    }

    private fun highlightLengthButtons(selected: Button, other1: Button, other2: Button) {
        selected.setBackgroundTintList(getColorStateList(R.color.pink_light))
        other1.setBackgroundTintList(getColorStateList(android.R.color.white))
        other2.setBackgroundTintList(getColorStateList(android.R.color.white))
    }

    //For Handling only Set A or Set B selected
    private fun returnPremadeDesign(setType: String) {
        // Insert Logic for Set A or Set B Design

        val resultIntent = Intent().apply {
            putExtra("setType", setType)
            putExtra("premadeDesign", true)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
