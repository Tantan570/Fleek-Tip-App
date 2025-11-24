package com.example.fleektip
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EyelashPickerActivity : AppCompatActivity() {

    private var selectedStyle: String? = null
    private var selectedThickness: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eyelash_picker) // your ScrollView layout

        // Classic Group
        val groupClassic = findViewById<RadioGroup>(R.id.groupClassic)
        val classicLight = findViewById<RadioButton>(R.id.classicLight)
        val classicMedium = findViewById<RadioButton>(R.id.classicMedium)
        val classicHeavy = findViewById<RadioButton>(R.id.classicHeavy)

        // Cat Eyes Group
        val groupCatEyes = findViewById<RadioGroup>(R.id.groupCatEyes)
        val catEyesLight = findViewById<RadioButton>(R.id.catEyesLight)
        val catEyesMedium = findViewById<RadioButton>(R.id.catEyesMedium)
        val catEyesHeavy = findViewById<RadioButton>(R.id.catEyesHeavy)

        // Doll Eyes Group
        val groupDollEyes = findViewById<RadioGroup>(R.id.groupDollEyes)
        val dollEyesLight = findViewById<RadioButton>(R.id.dollEyesLight)
        val dollEyesMedium = findViewById<RadioButton>(R.id.dollEyesMedium)
        val dollEyesHeavy = findViewById<RadioButton>(R.id.dollEyesHeavy)

        // Confirm Button
        val btnConfirm = findViewById<Button>(R.id.btnConfirmEyelash)

        // Helper function to clear other groups when a style is selected
        fun clearOtherGroups(except: RadioGroup) {
            val groups = listOf(groupClassic, groupCatEyes, groupDollEyes)
            for (g in groups) {
                if (g != except) g.clearCheck()
            }
        }

        // Style + Thickness Selection Logic
        val styleGroups = mapOf(
            groupClassic to "Classic",
            groupCatEyes to "Cat Eyes",
            groupDollEyes to "Doll Eyes"
        )

        for ((group, styleName) in styleGroups) {
            group.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    selectedStyle = styleName
                    clearOtherGroups(group)

                    selectedThickness = when (checkedId) {
                        R.id.classicLight, R.id.catEyesLight, R.id.dollEyesLight -> "Light"
                        R.id.classicMedium, R.id.catEyesMedium, R.id.dollEyesMedium -> "Medium"
                        R.id.classicHeavy, R.id.catEyesHeavy, R.id.dollEyesHeavy -> "Heavy"
                        else -> null
                    }

                    Toast.makeText(
                        this,
                        "Selected: $selectedStyle - $selectedThickness",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Confirm button logic
        btnConfirm.setOnClickListener {
            if (selectedStyle == null || selectedThickness == null) {
                Toast.makeText(this, "Please select both style and thickness", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("selectedStyle", selectedStyle)
                putExtra("selectedThickness", selectedThickness)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}