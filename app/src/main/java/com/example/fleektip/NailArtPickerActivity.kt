package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NailArtPickerActivity : AppCompatActivity() {

    private var selectedSet: String? = null
    private var selectedNailLength: String? = null
    private var selectedColor: String? = null
    private var isNailPolishMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.color_picker)

        // Nail Length Buttons
        val btnShort = findViewById<Button>(R.id.btnNailShort)
        val btnMedium = findViewById<Button>(R.id.btnNailMedium)
        val btnLong = findViewById<Button>(R.id.btnNailLong)

        // Nail Polish toggle
        val togglePolish = findViewById<Switch>(R.id.switchNailPolish)
        togglePolish.setOnCheckedChangeListener { _, isChecked ->
            isNailPolishMode = isChecked
            Toast.makeText(
                this,
                if (isChecked) "Nail Polish mode ON" else "Nail Polish mode OFF",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Nail Length Clicks
        btnShort.setOnClickListener { selectNailLength("short", btnShort, btnMedium, btnLong) }
        btnMedium.setOnClickListener { selectNailLength("medium", btnMedium, btnShort, btnLong) }
        btnLong.setOnClickListener { selectNailLength("long", btnLong, btnShort, btnMedium) }

        // Color Buttons
        val colors = mapOf(
            R.id.btnColorRed to "red",
            R.id.btnColorBlue to "blue",
            R.id.btnColorWhite to "white",
            R.id.btnColorBrown to "brown",
            R.id.btnColorPink to "pink"
        )

        colors.forEach { (id, colorName) ->
            findViewById<Button>(id).setOnClickListener {
                handleColorSelection(colorName)
            }
        }

        // Nail Art Set Buttons
        val shortSets = listOf(
            R.id.btnShortSet1, R.id.btnShortSet2, R.id.btnShortSet3,
            R.id.btnShortSet4, R.id.btnShortSet5
        )
        val mediumSets = listOf(
            R.id.btnMediumSet1, R.id.btnMediumSet2, R.id.btnMediumSet3,
            R.id.btnMediumSet4, R.id.btnMediumSet5
        )
        val longSets = listOf(
            R.id.btnLongSet1, R.id.btnLongSet2, R.id.btnLongSet3,
            R.id.btnLongSet4, R.id.btnLongSet5
        )

        shortSets.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                handlePremadeSet("short", index)
            }
        }
        mediumSets.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                handlePremadeSet("medium", index)
            }
        }
        longSets.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                handlePremadeSet("long", index)
            }
        }
    }

    private fun selectNailLength(length: String, selected: Button, other1: Button, other2: Button) {
        if (isNailPolishMode) {
            Toast.makeText(
                this,
                "Nail Polish mode active — cannot change length.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        selectedNailLength = length
        selectedSet = null
        highlightLengthButtons(selected, other1, other2)
    }

    private fun highlightLengthButtons(selected: Button, other1: Button, other2: Button) {
        selected.setBackgroundTintList(getColorStateList(R.color.pink_light))
        other1.setBackgroundTintList(getColorStateList(android.R.color.white))
        other2.setBackgroundTintList(getColorStateList(android.R.color.white))
    }

    private fun handleColorSelection(colorName: String) {
        if (isNailPolishMode) {
            // Nail polish mode logic: directly apply color
            val intent = Intent(this, NailArtActivity::class.java)
            val pushLens = when (colorName) {
                "red" -> "3d64a29b-2431-4904-a8b6-a8c8243786c0"
                "blue" -> "121f091e-7010-43b6-8700-2fe34a9b11e5"
                "white" -> "6cef79cb-364e-4b3b-bcdb-83792ec9ab5e"
                "brown" -> "9c045089-5cdc-4436-8988-f5d074b8806b"
                "pink" -> "9f29f58b-f6d5-4ff2-a6b0-524188d65b4f"
                else -> ""
            }
            intent.putExtra("PUSH_LENS", pushLens)
            startActivity(intent)
            setResult(RESULT_OK, Intent().apply {
                putExtra("selectedColor", colorName)
                putExtra("nailPolishMode", true)
            })
            finish()
            return
        }

        // Normal color + length
        if (selectedNailLength == null) {
            Toast.makeText(this, "Please select a Nail Length.", Toast.LENGTH_SHORT).show()
            return
        }

        val key = "$colorName-$selectedNailLength"
        val pushLens = when (key) {
            "red-short" -> "fba3cef5-2a77-44e9-bfe1-dae6e209822b"
            "red-medium" -> "e4fed180-be9b-4386-a000-54aa3da58328"
            "red-long" -> "70da906e-cf96-4ee2-8cff-ee62fd7de55b"

            "blue-short" -> "1a855963-a8a7-4616-b3ab-07e0f695950d"
            "blue-medium" -> "54ffcd95-3c1c-4693-8114-06441cd2112d"
            "blue-long" -> "ad50cfe0-306d-46d3-b542-89bddc7c8c43"

            "white-short" -> "499fd305-8373-4e31-9d6b-98a9f72f2e18"
            "white-medium" -> "6d1f5bef-5368-4032-b71d-cab34237a57a"
            "white-long" -> "467afe97-707d-433c-ac9c-964db83275d4"

            "brown-short" -> "b1a45548-82ad-4e29-9cfe-975fb2ee1421"
            "brown-medium" -> "f6adca42-c797-40c6-b877-86715f869b29"
            "brown-long" -> "68afa8b9-5869-4ba2-a92b-6e049f9fc26a"

            "pink-short" -> "0199a3bc-cfca-442b-9936-97bfdb72aa8c"
            "pink-medium" -> "58049e73-d623-48a3-b1f4-c152d6cf959e"
            "pink-long" -> "0acffcd2-747c-4a7d-aeaf-fdfb3626058a"

            else -> ""
        }

        if (pushLens.isNotEmpty()) {
            startActivity(Intent(this, NailArtActivity::class.java).apply {
                putExtra("PUSH_LENS", pushLens)
            })
        }

        setResult(RESULT_OK, Intent().apply {
            putExtra("selectedColor", colorName)
            putExtra("nailLength", selectedNailLength)
            putExtra("setType", "Custom")
        })
        finish()
    }

    private fun handlePremadeSet(length: String, index: Int) {
        if (isNailPolishMode) {
            Toast.makeText(this, "Turn off Nail Polish mode first.", Toast.LENGTH_SHORT).show()
            return
        }

        // Map index to Set letters
        val setLetter = when (index) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            4 -> "E"
            else -> "A"
        }

        selectedSet = setLetter
        selectedNailLength = null
        selectedColor = null

        Toast.makeText(this, "Set $setLetter selected", Toast.LENGTH_SHORT).show()

        // Apply AR lens based on length
        val intent = Intent(this, NailArtActivity::class.java)
        val pushLens = when("$length-$setLetter") {
            "short-A" -> "688c1e59-b88d-4239-a924-c23932aab922"
            "short-B" -> "ab4325bb-abf2-460e-9109-578b250b9658"
            "short-C" -> "bde53b47-4b6d-4395-91ed-a98b261f6a03"
            "short-D" -> "be91f30b-289b-473a-8274-cb4b0b712063"
            "short-E" -> "0657e3a8-7606-4814-bd2f-e14dc00beae3"

            "medium-A" -> "2e3e6bf7-8231-4ed2-b476-32302d14a520"
            "medium-B" -> "80bea708-21e6-4698-9c55-24e46eb8ec61"
            "medium-C" -> "7ee228e3-11d6-4104-9a6a-468ac98f866e"
            "medium-D" -> "6580e293-769b-4e7f-b40c-8496801e64a6"
            "medium-E" -> "ff5933c1-6c83-4aa1-af21-54a398044621"

            "long-A" -> "fe2a66bb-dc48-409d-89be-6ca9e6f8b265"
            "long-B" -> "a4583890-88a2-4ff0-91df-7949b675de92"
            "long-C" -> "47b672a9-4403-425d-8125-8f92386bf267"
            "long-D" -> "7a1b75ba-e232-4044-aedb-5872b06fc5ce"
            "long-E" -> "a0c06bd3-fcf4-48c6-972d-e406607186f3"

            else -> ""
        }
        if (pushLens.isNotEmpty()) {
            intent.putExtra("PUSH_LENS", pushLens)
            startActivity(intent)
        }

        setResult(RESULT_OK, Intent().apply {
            putExtra("setType", setLetter)
            putExtra("premadeDesign", true)
        })
        finish()
    }
}
