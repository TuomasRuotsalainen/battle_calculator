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

        adjacentBox.isChecked = true
        boxTerrainNone.isChecked = true

        // Detection Level Checkboxes
        val detectionLevelCheckboxes = listOf(adjacentBox, box4Hex, boxNone)
        detectionLevelCheckboxes.forEach { checkbox ->
            checkbox.setOnClickListener {
                // Uncheck all other checkboxes
                detectionLevelCheckboxes.filter { it != checkbox }.forEach { otherCheckbox ->
                    otherCheckbox.isChecked = false
                }
                // Handle the selected checkbox
                checkbox.isChecked = true
                // Handle the logic for the selected detection level
                //handleDetectionLevelSelection(checkbox.id)
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
                // Handle the selected checkbox
                checkbox.isChecked = true
                // Handle the logic for the selected target terrain
                //handleTargetTerrainSelection(checkbox.id)
            }
        }


    }


}













