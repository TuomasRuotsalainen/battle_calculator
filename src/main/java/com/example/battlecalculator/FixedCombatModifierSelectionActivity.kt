package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.*
import java.util.logging.Logger

class FixedCombatModifierSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_combat_modifiers)

        val gameState = getGameState(intent)

        val defenderOptions = findViewById<LinearLayout>(R.id.defenderOptionsLayout)
        val attackerOptions = findViewById<LinearLayout>(R.id.attackerOptionsLayout)

        val natoDefendView = findViewById<CheckBox>(R.id.checkBoxNATOCountries)
        val attackerAdvanceAxisView = findViewById<CheckBox>(R.id.attackerAdvanceAxis)
        val defenderAdvanceAxisView = findViewById<CheckBox>(R.id.defenderAdvanceAxis)

        if (gameState.activeAlliance == Alliances.NATO) {
            // Add nato option to defender
            defenderOptions.removeView(natoDefendView)
            attackerOptions.removeView(attackerAdvanceAxisView)
        } else {
            defenderOptions.removeView(defenderAdvanceAxisView)
        }

        val defenderResting = findViewById<CheckBox>(R.id.defenderResting)
        val defenderEngaged = findViewById<CheckBox>(R.id.defenderEngaged)
        val defenderHalfEngaged = findViewById<CheckBox>(R.id.defenderHalfEngaged)
        val defenderDelayed = findViewById<CheckBox>(R.id.defenderDelayed)
        val defenderOutOfCommandRange = findViewById<CheckBox>(R.id.defenderOutOfCommand)
        val defenderFrontLineCommand = findViewById<CheckBox>(R.id.defenderFrontLineCommand)

        val attackerHalfEngaged = findViewById<CheckBox>(R.id.attackerHalfEngaged)
        val attackerOutOfCommandRange = findViewById<CheckBox>(R.id.attackerOutOfCommand)
        val attackerFrontLineCommand = findViewById<CheckBox>(R.id.attackerFrontLineCommand)

        val buttonMap : HashMap<CheckBox, FixedModifierEnum> = HashMap()
        buttonMap[defenderResting] = FixedModifierEnum.DEFENDER_RESTING
        buttonMap[defenderEngaged] = FixedModifierEnum.DEFENDER_ENGAGED
        buttonMap[defenderHalfEngaged] = FixedModifierEnum.DEFENDER_HALF_ENGAGED
        buttonMap[defenderDelayed] = FixedModifierEnum.DEFENDER_DELAYED

        defenderResting.setOnClickListener {
            gameState.activeFixedModifiers.map[FixedModifierEnum.DEFENDER_RESTING] = defenderResting.isChecked
        }

        defenderEngaged.setOnClickListener {
            gameState.activeFixedModifiers.map[FixedModifierEnum.DEFENDER_ENGAGED] = defenderEngaged.isChecked
        }


        /*
        NATO_DEFENDS_MULTI_COUNTRY,
        DEFENDER_RESTING,
        DEFENDER_ENGAGED,
        DEFENDER_HALF_ENGAGED,
        DEFENDER_DELAYED,
        DEFENDER_OUT_OF_COMMAND_SCREEN_REC,
        DEFENDER_OUT_OF_COMMAND,
        DEFENDER_FRONT_LINE_COMMAND,
        PACT_ATTACKING_REAR,
        PACT_DEFENDING_REAR,
        ATTACKER_HALF_ENGAGED,
        ATTACKER_OUT_OF_COMMAND,
        ATTACKER_OUT_OF_COMMAND_SCREEN_REC,
        ATTACKER_FRONT_LINE_COMMAND,
        ATTACKER_USES_REC,
        ATTACKER_USES_SAPPERS,
        DEFENDER_USES_SAPPERS
        */

        /*
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
*/
        /*
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
        }*/


        val applyButton = findViewById<Button>(R.id.fixed_apply)

        val intent = Intent(this, UnitSelectionActivity::class.java)
        applyButton.setOnClickListener{
            //Toast.makeText(this, "Unit type: $radioButtonStr, attack strength: ${strengths.first}, defense strength: ${strengths.second}", Toast.LENGTH_LONG*10).show()
            throw Exception("Not implemented yet!")
            startActivity(intent)
            finish()
        }
    }


}