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

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var gameState = getGameStateIfExists(intent)

        val natoSelection = findViewById<RadioButton>(R.id.faction_selection_nato)
        val pactSelection = findViewById<RadioButton>(R.id.faction_selection_pact)

        val pactChemSel = findViewById<CheckBox>(R.id.pact_uses_chem)
        val natoChemSel = findViewById<CheckBox>(R.id.nato_uses_chem)

        val fog = findViewById<CheckBox>(R.id.fog_box)
        val precipitation = findViewById<CheckBox>(R.id.precipitation_box)

        val h00 = findViewById<RadioButton>(R.id.time00)
        val h03 = findViewById<RadioButton>(R.id.time03)
        val h06 = findViewById<RadioButton>(R.id.time06)
        val h09 = findViewById<RadioButton>(R.id.time09)
        val h12 = findViewById<RadioButton>(R.id.time12)
        val h15 = findViewById<RadioButton>(R.id.time15)
        val h18 = findViewById<RadioButton>(R.id.time18)
        val h21 = findViewById<RadioButton>(R.id.time21)

        var currentHour : HourEnum = HourEnum.H00

        val timeViews = listOf(h00, h03, h06, h09, h12, h15, h18, h21)
        val otherViews = listOf(natoChemSel, pactChemSel, fog, precipitation)

        fun setAllFields() {
            if (gameState == null) {
                natoSelection.isChecked = true
                h00.isChecked = true
            } else {

                if (gameState!!.activeAlliance == Alliances.NATO) {
                    natoSelection.isChecked = true
                } else {
                    pactSelection.isChecked = true
                }

                val currentConditions = gameState!!.conditions

                pactChemSel.isChecked = currentConditions.pactUsesChem
                natoChemSel.isChecked = currentConditions.natoUsesChem

                precipitation.isChecked = currentConditions.precipitation
                fog.isChecked = currentConditions.fog

                when (currentConditions.hourEnum) {
                    HourEnum.H00 -> h00.isChecked = true
                    HourEnum.H03 -> h03.isChecked = true
                    HourEnum.H06 -> h06.isChecked = true
                    HourEnum.H09 -> h09.isChecked = true
                    HourEnum.H12 -> h12.isChecked = true
                    HourEnum.H15 -> h15.isChecked = true
                    HourEnum.H18 -> h18.isChecked = true
                    HourEnum.H21 -> h21.isChecked = true
                }
            }
        }

        setAllFields()

        fun createConditions() : Conditions {
            return Conditions(pactChemSel.isChecked, natoChemSel.isChecked, currentHour, fog.isChecked, precipitation.isChecked)
        }

        val initialConditions = createConditions()

        if (gameState == null) {
            gameState = GameState(initialConditions)
        }


        fun updateConditions() {
            var conditions = createConditions()
            if (!conditions.isValid()) {
                fog.isChecked = false
                conditions = createConditions()
                if (!conditions.isValid()) {
                    throw Exception("Unable to create valid conditions")
                }
            }

            gameState.updateConditions(conditions)
            Log.d("Debug", "New conditions: ${conditions.toReadableString()}")
        }

        for (timeView in timeViews) {
            timeView.setOnClickListener {
                for (otherView in timeViews) {
                    otherView.isChecked = false
                }

                timeView.isChecked = true
                currentHour = strToHour(timeView.text.toString())

                updateConditions()
            }
        }

        for (view in otherViews) {
            view.setOnClickListener {
                updateConditions()
            }
        }

        natoSelection.setOnClickListener {
            gameState.activeAlliance = Alliances.NATO
        }

        pactSelection.setOnClickListener {
            gameState.activeAlliance = Alliances.PACT
        }

        val groundAttackButton = findViewById<Button>(R.id.groundAttackButton)
        val artillerBombardmentButton = findViewById<Button>(R.id.artillerBomabrdmentButton)

        val intent = Intent(this, UnitSelectionActivity::class.java)
        intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())

        groundAttackButton.setOnClickListener{
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }



        }
}