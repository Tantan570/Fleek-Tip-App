package com.example.fleektip

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ColorPickerActivity : AppCompatActivity() {
    private var selectedSet: String? = null
    private var selectedNailLength: String? = null
    private var selectedColor: String? = null
    private var isNailPolishMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.color_picker)

        // UI references
        val btnSetA = findViewById<Button>(R.id.btnSetA)
        val btnSetB = findViewById<Button>(R.id.btnSetB)
        val btnShort = findViewById<Button>(R.id.btnNailShort)
        val btnMedium = findViewById<Button>(R.id.btnNailMedium)
        val btnLong = findViewById<Button>(R.id.btnNailLong)
        val togglePolish = findViewById<Switch>(R.id.switchNailPolish)

        // Nail Polish Toggle
        togglePolish.setOnCheckedChangeListener { _, isChecked ->
            isNailPolishMode = isChecked
            if (isChecked) {
                Toast.makeText(this, "Nail Polish mode ON", Toast.LENGTH_SHORT).show()
                // can disable or reset other filters here if needed
            } else {
                Toast.makeText(this, "Nail Polish mode OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Set Buttons (Premade Designs)
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

            // Apply AR filter logic for "Set A" here before returning result
            val intent = Intent(this, NailArtActivity::class.java)
            intent.putExtra("PUSH_LENS", "2e3e6bf7-8231-4ed2-b476-32302d14a520")
            startActivity(intent)

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

            // Apply AR filter logic for "Set B" here before returning result
            val intent = Intent(this, NailArtActivity::class.java)
            intent.putExtra("PUSH_LENS", "80bea708-21e6-4698-9c55-24e46eb8ec61")
            startActivity(intent)


            returnPremadeDesign("B")
        }

        // Nail Length Buttons
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

        // Color Buttons
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
                    val keycolor = colorName
                    when (keycolor){
                        "red" -> {
                            val intent = Intent(this, NailArtActivity::class.java)
                            intent.putExtra("PUSH_LENS", "3d64a29b-2431-4904-a8b6-a8c8243786c0")
                            startActivity(intent)
                        }
                        "blue" -> {
                            val intent = Intent(this, NailArtActivity::class.java)
                            intent.putExtra("PUSH_LENS", "121f091e-7010-43b6-8700-2fe34a9b11e5")
                            startActivity(intent)
                        }
                        "white" -> {
                            val intent = Intent(this, NailArtActivity::class.java)
                            intent.putExtra("PUSH_LENS", "6cef79cb-364e-4b3b-bcdb-83792ec9ab5e")
                            startActivity(intent)
                        }
                        "brown" -> {
                            val intent = Intent(this, NailArtActivity::class.java)
                            intent.putExtra("PUSH_LENS", "9c045089-5cdc-4436-8988-f5d074b8806b")
                            startActivity(intent)
                        }
                        "pink" -> {
                            val intent = Intent(this, NailArtActivity::class.java)
                            intent.putExtra("PUSH_LENS", "9f29f58b-f6d5-4ff2-a6b0-524188d65b4f")
                            startActivity(intent)
                        }
                    }

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

                // Logic for Nail Length + Color
                val key = selectedColor + "-" + selectedNailLength

                when (key) {
                    // Nail Lengths for red
                    "red-short" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "fba3cef5-2a77-44e9-bfe1-dae6e209822b")
                        startActivity(intent)
                    }
                    "red-medium" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "e4fed180-be9b-4386-a000-54aa3da58328")
                        startActivity(intent)
                    }
                    "red-long" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "70da906e-cf96-4ee2-8cff-ee62fd7de55b")
                        startActivity(intent)
                    }
                    // Nail Lengths for blue
                    "blue-short" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "1a855963-a8a7-4616-b3ab-07e0f695950d")
                        startActivity(intent)
                    }
                    "blue-medium" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "54ffcd95-3c1c-4693-8114-06441cd2112d")
                        startActivity(intent)
                    }
                    "blue-long" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "ad50cfe0-306d-46d3-b542-89bddc7c8c43")
                        startActivity(intent)
                    }
                    //white
                    "white-short" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "499fd305-8373-4e31-9d6b-98a9f72f2e18")
                        startActivity(intent)
                    }
                    "white-medium" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "6d1f5bef-5368-4032-b71d-cab34237a57a")
                        startActivity(intent)
                    }
                    "white-long" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "467afe97-707d-433c-ac9c-964db83275d4")
                        startActivity(intent)
                    }
                    // Nail Length for brown
                    "brown-short" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "b1a45548-82ad-4e29-9cfe-975fb2ee1421")
                        startActivity(intent)
                    }
                    "brown-medium" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "f6adca42-c797-40c6-b877-86715f869b29")
                        startActivity(intent)
                    }
                    "brown-long" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "68afa8b9-5869-4ba2-a92b-6e049f9fc26a")
                        startActivity(intent)
                    }
                    //Nail Lengths for pink
                    "pink-short" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "0199a3bc-cfca-442b-9936-97bfdb72aa8c")
                        startActivity(intent)
                    }
                    "pink-medium" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "58049e73-d623-48a3-b1f4-c152d6cf959e")
                        startActivity(intent)
                    }
                    "pink-long" -> {
                        val intent = Intent(this, NailArtActivity::class.java)
                        intent.putExtra("PUSH_LENS", "0acffcd2-747c-4a7d-aeaf-fdfb3626058a")
                        startActivity(intent)
                    }
                }

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


    private fun highlightLengthButtons(selected: Button, other1: Button, other2: Button) {
        selected.setBackgroundTintList(getColorStateList(R.color.pink_light))
        other1.setBackgroundTintList(getColorStateList(android.R.color.white))
        other2.setBackgroundTintList(getColorStateList(android.R.color.white))
    }

    //For Handling only when Set A or Set B selected
    private fun returnPremadeDesign(setType: String) {
        val resultIntent = Intent().apply {
            putExtra("setType", setType)
            putExtra("premadeDesign", true)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
