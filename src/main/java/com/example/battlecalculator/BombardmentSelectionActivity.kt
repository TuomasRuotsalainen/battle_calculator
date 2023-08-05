package com.example.battlecalculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class BombardmentSelectionActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bombardment_selection)

        val gameState = getGameState(intent)

        val setPostureButton = findViewById<Button>(R.id.setPosture)

        val postureWords = PostureWords.values().toList()
        val iconNames = postureWords.map { it.toString() + "_smaller" }

        val iconResources = iconNames.map {
            this.resources.getIdentifier(it, "drawable", this.packageName)
        }

        val readablePostures = postureWords.map { postureWord ->
            val word = postureWord.name
            val capitalized = word.split("_").joinToString(" ") { it.capitalize() }
            "    $capitalized"
        }

        var icons = iconResources
        var items = readablePostures

        var selectedPosture : PostureEnum = PostureEnum.TAC
        var selectedPostureIconName : String = iconNames[6]

        val currentPostureView = findViewById<ImageView>(R.id.currentPostureView)

        fun updateCurrentPostureImage() {
            val currentPostureDrawable =
                Images.getDrawable(selectedPostureIconName, this, applicationContext, applicationInfo)
                    ?: throw Exception("Unable to get drawable for $selectedPostureIconName")

            currentPostureView.setImageDrawable(currentPostureDrawable)
        }

        updateCurrentPostureImage()

        val explanation = findViewById<TextView>(R.id.explanation)

        val adjacentBox = findViewById<CheckBox>(R.id.detection_lvl_adjacent)
        val box4Hex = findViewById<CheckBox>(R.id.detection_lvl_4hex)
        val boxNone = findViewById<CheckBox>(R.id.detection_lvl_none)
        val boxCombatUnit = findViewById<CheckBox>(R.id.target_attributes_combat)
        val boxSupportUnit = findViewById<CheckBox>(R.id.target_attributes_support)
        val boxSoftTarget = findViewById<CheckBox>(R.id.target_attributes_soft)
        val boxRoadPosture = findViewById<CheckBox>(R.id.detection_mod_road)
        val boxPostures = findViewById<CheckBox>(R.id.detection_mod_postures)
        val boxCloseArty = findViewById<CheckBox>(R.id.detection_mod_close_arty)
        val boxFARP = findViewById<CheckBox>(R.id.detection_mod_farp)
        val boxCity = findViewById<CheckBox>(R.id.detection_mod_city)
        val boxMovingHQ = findViewById<CheckBox>(R.id.detection_mod_movinghq)
        val boxSpottedHQ = findViewById<CheckBox>(R.id.detection_mod_spottedhq)
        val boxTerrainCity = findViewById<CheckBox>(R.id.target_terrain_city)
        val boxTerrainDef3 = findViewById<CheckBox>(R.id.target_terrain_def3)
        val boxTerrainTown = findViewById<CheckBox>(R.id.target_terrain_town)
        val boxTerrainDef1 = findViewById<CheckBox>(R.id.target_terrain_def1)
        val boxTerrainForest = findViewById<CheckBox>(R.id.target_terrain_forest)
        val boxTerrainNone = findViewById<CheckBox>(R.id.target_terrain_none)

        val terranCheckboxMap = mutableMapOf<CheckBox, TerrainEnum>()
        terranCheckboxMap[boxTerrainCity] = TerrainEnum.CITY
        terranCheckboxMap[boxTerrainDef3] = TerrainEnum.DEFENSE3
        terranCheckboxMap[boxTerrainTown] = TerrainEnum.TOWN
        terranCheckboxMap[boxTerrainDef1] = TerrainEnum.DEFENSE1
        terranCheckboxMap[boxTerrainForest] = TerrainEnum.FOREST
        terranCheckboxMap[boxTerrainNone] = TerrainEnum.PLAIN

        val detectionLevelModifierMap = mutableMapOf<CheckBox, DetectionModifiers>()
        detectionLevelModifierMap[boxRoadPosture] = DetectionModifiers.ROAD
        detectionLevelModifierMap[boxPostures] = DetectionModifiers.BARR_CSUP_MASL
        detectionLevelModifierMap[boxCloseArty] = DetectionModifiers.ARTILLERY_WITHIN_2
        detectionLevelModifierMap[boxFARP] = DetectionModifiers.FARP
        detectionLevelModifierMap[boxCity] = DetectionModifiers.CITY // TODO add this to terrain enums instead?
        detectionLevelModifierMap[boxMovingHQ] = DetectionModifiers.MOVING_HQ
        detectionLevelModifierMap[boxSpottedHQ] = DetectionModifiers.SPOTTED_HQ

        val calculator = Calculator()

        // Target Terrain Checkboxes
        val detectionLevelModCheckboxes = listOf(
            boxRoadPosture,
            boxPostures,
            boxCloseArty,
            boxFARP,
            boxCity,
            boxMovingHQ,
            boxSpottedHQ
        )

        gameState.detectionLevelModifiers = mutableListOf()
        gameState.hexTerrain = HexTerrain(mutableListOf(TerrainEnum.PLAIN))

        boxNone.isChecked = true
        boxTerrainNone.isChecked = true

        boxCombatUnit.isChecked = true
        var combatUnitSelected = true

        gameState.detectionLevel = DetectionLevel.COMBAT_UNIT_OTHER


        fun updateExplanation() {
            val newDetectionLevel = calculator.calculateDetectionLevel(gameState.detectionLevel!!, gameState.detectionLevelModifiers!!)
            explanation.text = "Detection: $newDetectionLevel, Bombardment modifier: 4\nHigher the better!\nMean result for bombardment of 2:\n0 attrition, target Half-Enganged"
        }

        updateExplanation()

        val dummyUnit = Unit("ARMOR", "1-1", "3pz_7_74", 1)
        gameState.attackingUnit = UnitState(dummyUnit, selectedPosture, null, null, false, 0, null, RiverCrossingTypeEnum.NONE, false) // This is a hacky way to deliver posture information

        setPostureButton.setOnClickListener {
            Helpers.showRadioButtonDialog(
                context = this,
                items = items,
                icons = icons,
                title = "Select target's posture",
                buttonText = "Ok",
                negativeButtonText = "Cancel",
                selectedCallback = { selectedColor ->
                    // This will be called when the user selects a color
                    //Toast.makeText(this, "You selected: $selectedColor", Toast.LENGTH_SHORT).show()
                    val postureIndex = readablePostures.indexOf(selectedColor)
                    selectedPosture = PostureEnum.values()[postureIndex]
                    selectedPostureIconName = iconNames[postureIndex]
                    updateCurrentPostureImage()
                    gameState.attackingUnit!!.posture = selectedPosture
                    //currentUnitState.posture = selectedPosture
                    //val textContent = getTextViewString(currentUnitState)
                    //textView.text = textContent
                    updateExplanation()
                },
                negativeCallback = {
                    // This will be called when the user taps the negative button (optional)
                    Toast.makeText(this, "No color was selected", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Detection Level Checkboxes
        val detectionLevelCheckboxes = listOf(adjacentBox, box4Hex, boxNone)
        detectionLevelCheckboxes.forEach { checkbox ->
            checkbox.setOnClickListener {
                // Uncheck all other checkboxes
                detectionLevelCheckboxes.filter { it != checkbox }.forEach { otherCheckbox ->
                    otherCheckbox.isChecked = false
                }
                checkbox.isChecked = true
                when (checkbox.id) {
                    R.id.detection_lvl_adjacent -> {
                        if (combatUnitSelected) {
                            gameState.detectionLevel = DetectionLevel.COMBAT_UNIT_ADJACENT
                        } else {
                            gameState.detectionLevel = DetectionLevel.SUPPORT_UNIT_ADJACENT
                        }
                    }
                    R.id.detection_lvl_4hex -> {
                        if (combatUnitSelected) {
                            gameState.detectionLevel = DetectionLevel.COMBAT_UNIT_WITHIN_4
                        } else {
                            gameState.detectionLevel = DetectionLevel.SUPPORT_UNIT_WITHIN_4
                        }
                    }
                    R.id.detection_lvl_none -> {
                        if (combatUnitSelected) {
                            gameState.detectionLevel = DetectionLevel.COMBAT_UNIT_OTHER
                        } else {
                            gameState.detectionLevel = DetectionLevel.SUPPORT_UNIT_OTHER
                        }
                    }
                    else -> {
                        throw Exception("This id not expected: ${checkbox.id}")
                    }
                }

                updateExplanation()
            }
        }

        // Target Terrain Checkboxes
        val targetTerrainCheckboxes = listOf(
            boxTerrainCity,
            boxTerrainDef3,
            boxTerrainTown,
            boxTerrainDef1,
            boxTerrainForest,
            boxTerrainNone
        )
        targetTerrainCheckboxes.forEach { checkbox ->
            checkbox.setOnClickListener {
                // Uncheck all other checkboxes
                targetTerrainCheckboxes.filter { it != checkbox }.forEach { otherCheckbox ->
                    otherCheckbox.isChecked = false
                }
                checkbox.isChecked = true
                val hexTerrain = terranCheckboxMap[checkbox]
                    ?: throw Exception("Couldn't find hexterrain for checkbox ${checkbox}")
                gameState.hexTerrain = HexTerrain(mutableListOf(hexTerrain))

                updateExplanation()
            }
        }

        boxCombatUnit.setOnClickListener {
            boxSupportUnit.isChecked = !boxCombatUnit.isChecked
            combatUnitSelected = boxCombatUnit.isChecked
            updateExplanation()
        }

        boxSupportUnit.setOnClickListener {
            boxCombatUnit.isChecked = !boxSupportUnit.isChecked
            combatUnitSelected = boxCombatUnit.isChecked
            updateExplanation()
        }

        detectionLevelModCheckboxes.forEach{ checkBox ->
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    gameState.detectionLevelModifiers!!.add(detectionLevelModifierMap[checkBox]!!)
                } else {
                    gameState.detectionLevelModifiers!!.remove(detectionLevelModifierMap[checkBox])
                }

                updateExplanation()
            }
        }

        val cancelBtn = findViewById<Button>(R.id.btncancel)
        val commitBtn = findViewById<Button>(R.id.commit)

        cancelBtn.setOnClickListener{
            val nextIntent = Intent(this, MainActivity::class.java)
            nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            startActivity(nextIntent)
            finish()
        }

        commitBtn.setOnClickListener{
            if (gameState.attackingUnit == null) {
                throw Exception("Attacking (target) unit is null")
            } else {
                Log.d("Attacking unit not null", gameState.attackingUnit.toString())
            }
            val nextIntent = Intent(this, CombatSupportActivity::class.java)
            nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            nextIntent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.BOMBARDMENT.toString())
            startActivity(nextIntent)
            finish()
        }

    }


}













