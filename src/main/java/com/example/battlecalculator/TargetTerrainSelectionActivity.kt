package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.logging.Logger

class TargetTerrainSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_terrain_selection)

        val gameState = getGameState(intent)

        val forestCheck = findViewById<CheckBox>(R.id.checkBoxForest)
        val plainCheck = findViewById<CheckBox>(R.id.checkBoxPlain)
        val townCheck = findViewById<CheckBox>(R.id.checkBoxTown)
        val cityCheck = findViewById<CheckBox>(R.id.checkBoxCity)
        val swampCheck = findViewById<CheckBox>(R.id.checkBoxSwamp)
        val minorRiverCheck = findViewById<CheckBox>(R.id.checkBoxMinorRiver)
        val majorRiverCheck = findViewById<CheckBox>(R.id.checkBoxMajorRiver)

        val defenseWorks1Check = findViewById<CheckBox>(R.id.checkBoxDefenseWorks1)
        val defenseWorks3Check = findViewById<CheckBox>(R.id.checkBoxDefenseWorks3)
        val bridgeCheck = findViewById<CheckBox>(R.id.checkBoxBridgeExists)

        plainCheck.isChecked = true

        defenseWorks3Check.setOnClickListener {
            defenseWorks1Check.isChecked = false
        }

        defenseWorks1Check.setOnClickListener {
            defenseWorks3Check.isChecked = false
        }

        majorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }
            if (majorRiverCheck.isChecked) {
                minorRiverCheck.isChecked = false
            }

        }

        minorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }

            if (minorRiverCheck.isChecked) {
                majorRiverCheck.isChecked = false
            }
        }

        bridgeCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }
        }


        val applyButton = findViewById<Button>(R.id.terrain_apply)

        val intent = if (gameState.getDisengagingDefender() != null) {
            Intent(this, DisengagementActivity::class.java)
        } else {
            Intent(this, FixedCombatModifierSelectionActivity::class.java)
        }

        applyButton.setOnClickListener{

            val terrainList = mutableListOf<TerrainEnum>()

            if (forestCheck.isChecked) {
                terrainList.add(TerrainEnum.FOREST)
            }

            if (plainCheck.isChecked) {
                terrainList.add(TerrainEnum.PLAIN)
            }

            if (townCheck.isChecked) {
                terrainList.add(TerrainEnum.TOWN)
            }

            if (cityCheck.isChecked) {
                terrainList.add(TerrainEnum.CITY)
            }

            if (swampCheck.isChecked) {
                terrainList.add(TerrainEnum.SWAMP)
            }

            if (minorRiverCheck.isChecked) {
                terrainList.add(TerrainEnum.MINORRIVER)
            }

            if (majorRiverCheck.isChecked) {
                terrainList.add(TerrainEnum.MAJORRIVER)
            }

            if (defenseWorks1Check.isChecked) {
                terrainList.add(TerrainEnum.DEFENSE1)
            }

            if (defenseWorks3Check.isChecked) {
                terrainList.add(TerrainEnum.DEFENSE3)
            }

            if (bridgeCheck.isChecked) {
                terrainList.add(TerrainEnum.BRIDGE)
            }

            gameState.hexTerrain = HexTerrain(terrainList)

            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            startActivity(intent)
            finish()
        }
    }
}