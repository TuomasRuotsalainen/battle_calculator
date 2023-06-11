package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.*
import java.util.logging.Logger

class TargetTerrainSelectionActivity : AppCompatActivity() {

    // TODO add MP cost information for attacking unit regarding river crossing (and possible other attributes as well)
    // TODO continued at least attack type, river crossing stuff, defensive works, possibly breakthroughs affect MP expenditure
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

        val hastyCrossingButton = findViewById<RadioButton>(R.id.radio_river_crossing_hasty)
        val preparedCrossingButton = findViewById<RadioButton>(R.id.radio_river_crossing_prepared)

        val riverCrossingRadio = findViewById<RadioGroup>(R.id.crossingTypeRadio)

        var riverCrossingTypeEnum : RiverCrossingTypeEnum = RiverCrossingTypeEnum.NONE

        fun removeRiverCrossingSelection() {
            hastyCrossingButton.isChecked = false
            hastyCrossingButton.isEnabled = false
            preparedCrossingButton.isChecked = false
            preparedCrossingButton.isEnabled = false
            riverCrossingTypeEnum = RiverCrossingTypeEnum.NONE
        }

        fun addRiverCrossingSelectionIfNeeded() {
            if (bridgeCheck.isChecked) {
                return
            }

            if (majorRiverCheck.isChecked) {
                hastyCrossingButton.isChecked = false
                hastyCrossingButton.isEnabled = false
                preparedCrossingButton.isEnabled = true
                preparedCrossingButton.isChecked = true
                riverCrossingTypeEnum = RiverCrossingTypeEnum.PREPARED
            } else {
                hastyCrossingButton.isChecked = true
                hastyCrossingButton.isEnabled = true
                preparedCrossingButton.isEnabled = true
                preparedCrossingButton.isChecked = false
                riverCrossingTypeEnum = RiverCrossingTypeEnum.HASTY
            }
        }

        hastyCrossingButton.setOnClickListener {
            if (hastyCrossingButton.isChecked) {
                riverCrossingTypeEnum = RiverCrossingTypeEnum.HASTY
                preparedCrossingButton.isChecked = false
            }
        }

        preparedCrossingButton.setOnClickListener {
            if (preparedCrossingButton.isChecked) {
                riverCrossingTypeEnum = RiverCrossingTypeEnum.PREPARED
                hastyCrossingButton.isChecked = false
            }
        }

        removeRiverCrossingSelection()

        plainCheck.isChecked = true

        defenseWorks3Check.setOnClickListener {
            defenseWorks1Check.isChecked = false
        }

        defenseWorks1Check.setOnClickListener {
            defenseWorks3Check.isChecked = false
        }

        majorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                removeRiverCrossingSelection()
                bridgeCheck.isChecked = false
            }
            if (majorRiverCheck.isChecked) {
                addRiverCrossingSelectionIfNeeded()
                minorRiverCheck.isChecked = false
            }

        }

        minorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                removeRiverCrossingSelection()
                bridgeCheck.isChecked = false
            }

            if (minorRiverCheck.isChecked) {
                majorRiverCheck.isChecked = false
                addRiverCrossingSelectionIfNeeded()
            }
        }

        bridgeCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }

            if (bridgeCheck.isChecked) {
                removeRiverCrossingSelection()
            } else if(majorRiverCheck.isChecked || minorRiverCheck.isChecked) {
                addRiverCrossingSelectionIfNeeded()
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

            gameState.attackingUnit!!.riverCrossingType = riverCrossingTypeEnum

            fun launchNextActivity() {
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                startActivity(intent)
                finish()
            }

            if (riverCrossingTypeEnum != RiverCrossingTypeEnum.NONE) {

                val movement = Movement()

                val obstacle = gameState.hexTerrain!!.getObstacle(riverCrossingTypeEnum)
                    ?: throw Exception("Obstacle shouldn't be null here")

                val dice = Dice()
                val result = dice.roll()

                val attritionFromRiverCrossing = movement.getAttritionForRiverCrossing(result, obstacle, gameState.attackingUnit!!)

                var dialogText = "River crossing dice roll: ${result.get()}\n\n"
                dialogText += if (attritionFromRiverCrossing > 0) {
                    "River crossing resulted $attritionFromRiverCrossing attrition for the attacking unit.\n\nAdd that attrition now."
                } else {
                    "River crossing didn't cause any attrition for the attacking unit."
                }

                Helpers.showInfoDialog(this, dialogText, "Understood", null, {
                    launchNextActivity()
                })
            } else {
                launchNextActivity()
            }


        }


    }
}