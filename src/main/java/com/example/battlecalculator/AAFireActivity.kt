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

        val unitSelectionType = Communication.getUnitSelectionType(intent)

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

        fun executeAAForAircraft(cs : CombatSupport, aaValue : Int) {
            val die = DieRoll()
            val result = aaFire.getResult(die, aaValue)
            val aborted = result.getAbortedAirPoints()
            val destroyed = result.getShotDownAirPoints()

            cs.adjustAirPoints(cs.getAirPoints()-aborted-destroyed)
        }

        fun executeAAForHelicopter(aaValue : Int, die : DieRoll) {
            val result = aaFire.getResult(die, aaValue)
            val attritionToHelos = result.getAttritionToHelicopters()


        }

        val aaApply = findViewById<Button>(R.id.apply)

        aaApply.setOnClickListener{

            if (attackerCS.getAirPoints() > 0) {
                //executeAAForAircraft()
            }

            val intent : Intent = Intent(this, AAFireActivity::class.java)
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }
    }

    private fun getIntFromTextField(editText: EditText) : Int {
        return editText.text.toString().toIntOrNull() ?: throw Exception("Encountered null text field with value ${editText.text}")
    }

}