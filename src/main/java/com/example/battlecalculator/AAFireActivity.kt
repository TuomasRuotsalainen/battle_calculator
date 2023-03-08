package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import kotlin.Unit

class AAFireActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aa_fire)

        var gameState = getGameState(intent)

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }

        val attackerAir = findViewById<LinearLayout>(R.id.attacker_aircraft)
        val attackerHelo1 = findViewById<LinearLayout>(R.id.attacker_helo_1)
        val attackerHelo2 = findViewById<LinearLayout>(R.id.attacker_helo_2)

        val attackerCS = gameState.combatSupport!!.getAttackerCombatSupport()

        val defenderAir = findViewById<LinearLayout>(R.id.defender_aircraft)
        val defenderHelo1 = findViewById<LinearLayout>(R.id.defender_helo_1)
        val defenderHelo2 = findViewById<LinearLayout>(R.id.defender_helo_2)

        val defenderCS = gameState.combatSupport!!.getAttackerCombatSupport()

        class AASetting(val aaToAttackerAir : Int, val aaToAttackerHelo1 : Int, val aaToAttackerHelo2 : Int, val aaToDefenderAir : Int, val aaToDefenderHelo1 : Int, val aaToDefenderHelo2 : Int,) {}

        fun getAASetting() : AASetting {
            return AASetting(
                getIntFromTextField(findViewById(R.id.aa_against_attacker_aircraft_input)),
                getIntFromTextField(findViewById(R.id.aa_against_attacker_heli_1_input)),
                getIntFromTextField(findViewById(R.id.aa_against_attacker_heli_2_input)),
                getIntFromTextField(findViewById(R.id.aa_against_defender_aircraft_input)),
                getIntFromTextField(findViewById(R.id.aa_against_defender_heli_1_input)),
                getIntFromTextField(findViewById(R.id.aa_against_defender_heli_2_input)),
            )
        }

        fun displayAAResults(aaResultAttackerAircraft : Tables.AAFire.Result, aaResultDefenderAircraft : Tables.AAFire.Result) {
            val attackerAircraftView = findViewById<TextView>(R.id.aa_against_attacker_aircraft_input)
            attackerAircraftView.text = "Attacker air points\naborted: ${aaResultAttackerAircraft.getAbortedAirPoints()}\nshot down: ${aaResultAttackerAircraft.getShotDownAirPoints()}"

            val defenderAircraftView = findViewById<TextView>(R.id.aa_against_attacker_aircraft_input)
            defenderAircraftView.text = "Defender air points\naborted: ${aaResultDefenderAircraft.getAbortedAirPoints()}\nshot down: ${aaResultDefenderAircraft.getShotDownAirPoints()}"

        }

        if (attackerCS!!.getAirPoints() == 0) {
            attackerAir.removeAllViews()
        }

        if (attackerCS.getHelicopterCount() == 0) {
            attackerHelo1.removeAllViews()
        } else if (attackerCS.getHelicopterCount() == 1) {
            attackerHelo2.removeAllViews()
        }

        if (defenderCS!!.getAirPoints() == 0) {
            defenderAir.removeAllViews()
        }

        if (defenderCS.getHelicopterCount() == 0) {
            defenderHelo1.removeAllViews()
        } else if (defenderCS.getHelicopterCount() == 1) {
            defenderHelo2.removeAllViews()
        }

        val aaFire = Tables.AAFire()

        fun executeAAForAircraft(aaValue : Int): Tables.AAFire.Result {
            val die = DieRoll()
            return aaFire.getResult(die, aaValue)
        }

        fun executeAAForHelicopter(aaValue : Int, die : DieRoll) {
            val result = aaFire.getResult(die, aaValue)
            val attritionToHelos = result.getAttritionToHelicopters()


        }

        var isFirstClick = true

        val aaApply = findViewById<Button>(R.id.apply)

        aaApply.setOnClickListener{

            if (isFirstClick) {
                val aaSetting = getAASetting()
                val attackerAirResult = executeAAForAircraft(aaSetting.aaToAttackerAir)
                val defenderAirResult = executeAAForAircraft(aaSetting.aaToDefenderAir)

                displayAAResults(attackerAirResult, defenderAirResult)

                val attackerCS = gameState.combatSupport!!.getAttackerCombatSupport()
                val airPointsBefore = gameState.combatSupport!!.getAttackerCombatSupport()!!.getAirPoints()
                attackerCS!!.adjustAirPoints(attackerAirResult)

                val airPointsAfter = gameState.combatSupport!!.getAttackerCombatSupport()!!.getAirPoints()

                if (airPointsBefore - (attackerAirResult.getAbortedAirPoints()+attackerAirResult.getShotDownAirPoints()) != airPointsAfter) {
                    throw Exception("This way of setting things is not working")
                }

                aaApply.text = "Apply AA resuls"

                isFirstClick = false

            } else {
                val intent : Intent = Intent(this, AAFireActivity::class.java)
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

                startActivity(intent)
                finish()
            }



        }
    }

    private fun getIntFromTextField(editText: EditText) : Int {
        return editText.text.toString().toIntOrNull() ?: throw Exception("Encountered null text field with value ${editText.text}")
    }

}