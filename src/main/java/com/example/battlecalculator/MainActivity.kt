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
        gameState?.reset()

        //  TODO make sure rule section 31. (defensive works) is handled
        // TODO handle 6.3.3

        val natoSelection = findViewById<RadioButton>(R.id.faction_selection_nato)
        val pactSelection = findViewById<RadioButton>(R.id.faction_selection_pact)

        val pactReleasesChem = findViewById<CheckBox>(R.id.pact_uses_chem)

        val fog = findViewById<CheckBox>(R.id.fog_box)
        val precipitation = findViewById<CheckBox>(R.id.precipitation_box)

        val d0 = findViewById<RadioButton>(R.id.day0)
        val d1 = findViewById<RadioButton>(R.id.day1)
        val d2 = findViewById<RadioButton>(R.id.day2)
        val d3 = findViewById<RadioButton>(R.id.day3)
        val d4 = findViewById<RadioButton>(R.id.day4)

        val h00 = findViewById<RadioButton>(R.id.time00)
        val h03 = findViewById<RadioButton>(R.id.time03)
        val h06 = findViewById<RadioButton>(R.id.time06)
        val h09 = findViewById<RadioButton>(R.id.time09)
        val h12 = findViewById<RadioButton>(R.id.time12)
        val h15 = findViewById<RadioButton>(R.id.time15)
        val h18 = findViewById<RadioButton>(R.id.time18)
        val h21 = findViewById<RadioButton>(R.id.time21)

        var currentHour : HourEnum = HourEnum.H00
        var currentDay : DayEnum = DayEnum.D0

        val timeViews = listOf(h00, h03, h06, h09, h12, h15, h18, h21)
        val dayViews = listOf(d0, d1, d2, d3, d4)
        val otherViews = listOf(pactReleasesChem, fog, precipitation)

        var chemUsageStartedDay : DayEnum? = null
        var chemUsageStartedHour : HourEnum? = null

        var chemUsageStartedBefore = false

        fun setAllFields() {
            if (gameState == null) {
                natoSelection.isChecked = true
                h00.isChecked = true
                d0.isChecked = true
            } else {

                if (gameState!!.activeAlliance == Alliances.NATO) {
                    natoSelection.isChecked = true
                } else {
                    pactSelection.isChecked = true
                }

                val currentConditions = gameState!!.conditions

                chemUsageStartedDay = currentConditions.chemUsageStartDay
                chemUsageStartedHour = currentConditions.chemUsageStartHour

                pactReleasesChem.isChecked = currentConditions.doesPactUseChem()
                if (pactReleasesChem.isChecked) {
                    pactReleasesChem.isEnabled = false
                    chemUsageStartedBefore = true
                }

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

                when (currentConditions.dayEnum) {
                    DayEnum.D0 -> d0.isChecked = true
                    DayEnum.D1 -> d1.isChecked = true
                    DayEnum.D2 -> d2.isChecked = true
                    DayEnum.D3 -> d3.isChecked = true
                    DayEnum.D4 -> d4.isChecked = true
                }
            }
        }

        setAllFields()

        fun createConditions() : Conditions {
            return Conditions(currentDay, currentHour, fog.isChecked, precipitation.isChecked, chemUsageStartedDay, chemUsageStartedHour)
        }

        val initialConditions = createConditions()

        if (gameState == null) {
            gameState = GameState(initialConditions)
        }


        fun updateConditions() {
            if (pactReleasesChem.isChecked && !chemUsageStartedBefore) {
                chemUsageStartedDay = currentDay
                chemUsageStartedHour = currentHour
            } else if (!chemUsageStartedBefore) {
                chemUsageStartedDay = null
                chemUsageStartedHour = null
            }

            var conditions = createConditions()
            if (!conditions.isValid()) {
                fog.isChecked = false
                conditions = createConditions()
                if (!conditions.isValid()) {
                    throw Exception("Unable to create valid conditions")
                }
            }

            gameState.updateConditions(conditions)
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

        for (dayView in dayViews) {
            dayView.setOnClickListener {
                for (otherView in dayViews) {
                    otherView.isChecked = false
                }

                dayView.isChecked = true
                currentDay = strToDay(dayView.text.toString())

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



        groundAttackButton.setOnClickListener{
            val intent = Intent(this, UnitSelectionActivityInput::class.java)
            intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())

            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }

        artillerBombardmentButton.setOnClickListener{
            val intent = Intent(this, BombardmentSelectionActivity::class.java)

            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }



        }
}