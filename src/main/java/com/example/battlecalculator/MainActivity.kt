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

        val natoSelection = findViewById<RadioButton>(R.id.faction_selection_nato)
        val pactSelection = findViewById<RadioButton>(R.id.faction_selection_pact)
        natoSelection.isChecked = true



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

        h00.isChecked = true
        var currentHour : HourEnum = HourEnum.H00

        val timeViews = listOf(h00, h03, h06, h09, h12, h15, h18, h21)
        val otherViews = listOf(natoChemSel, pactChemSel, fog, precipitation)

        fun createConditions() : Conditions {
            return Conditions(pactChemSel.isChecked, natoChemSel.isChecked, currentHour, fog.isChecked, precipitation.isChecked)
        }

        val initialConditions = createConditions()
        val state = GameState(initialConditions)


        fun updateConditions() {
            val conditions = createConditions()
            state.updateConditions(conditions)
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
            state.activeAlliance = Alliances.NATO
        }

        pactSelection.setOnClickListener {
            state.activeAlliance = Alliances.PACT
        }

        val groundAttackButton = findViewById<Button>(R.id.groundAttackButton)
        val artillerBombardmentButton = findViewById<Button>(R.id.artillerBomabrdmentButton)

        val intent = Intent(this, UnitSelectionActivity::class.java)
        intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())

        groundAttackButton.setOnClickListener{
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), state.getStateString())

            startActivity(intent)
            finish()
        }



        }
}