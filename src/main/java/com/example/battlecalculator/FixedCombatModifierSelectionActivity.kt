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
        val calculator = Calculator()

        val defenderOptions = findViewById<LinearLayout>(R.id.defenderOptionsLayout)
        val attackerOptions = findViewById<LinearLayout>(R.id.attackerOptionsLayout)

        val natoDefendView = findViewById<CheckBox>(R.id.checkBoxNATOCountries)
        val defenderAdvanceAxisView = findViewById<CheckBox>(R.id.defenderAdvanceAxis)
        val defenderResting = findViewById<CheckBox>(R.id.defenderResting)
        val defenderDelayed = findViewById<CheckBox>(R.id.defenderDelayed)
        val defenderHalfEngaged = findViewById<CheckBox>(R.id.defenderHalfEngaged)
        val defenderEngaged = findViewById<CheckBox>(R.id.defenderEngaged)
        val defenderSappers = findViewById<CheckBox>(R.id.defenderSappers)

        val attackerAdvanceAxisView = findViewById<CheckBox>(R.id.attackerAdvanceAxis)
        val attackerSappers = findViewById<CheckBox>(R.id.attackerSappers)
        val attackerRecce = findViewById<CheckBox>(R.id.attackerRecce)
        val attackerHalfEngaged = findViewById<CheckBox>(R.id.attackerHalfEngaged)

        val resultView = findViewById<TextView>(R.id.resultView)

        if (gameState.activeAlliance == Alliances.NATO) {
            // Add nato option to defender
            defenderOptions.removeView(natoDefendView)
            attackerOptions.removeView(attackerAdvanceAxisView)
            attackerOptions.removeView(attackerSappers)
        } else {
            defenderOptions.removeView(defenderAdvanceAxisView)
            defenderOptions.removeView(defenderSappers)
        }

        val buttonMap : HashMap<CheckBox, FixedModifierEnum> = HashMap()
        buttonMap[defenderResting] = FixedModifierEnum.DEFENDER_RESTING
        buttonMap[defenderDelayed] = FixedModifierEnum.DEFENDER_DELAYED
        buttonMap[defenderHalfEngaged] = FixedModifierEnum.DEFENDER_HALF_ENGAGED
        buttonMap[defenderEngaged] = FixedModifierEnum.DEFENDER_ENGAGED
        buttonMap[attackerAdvanceAxisView] = FixedModifierEnum.PACT_ATTACKING_REAR
        buttonMap[defenderAdvanceAxisView] = FixedModifierEnum.PACT_DEFENDING_REAR
        buttonMap[natoDefendView] = FixedModifierEnum.NATO_DEFENDS_MULTI_COUNTRY
        buttonMap[defenderSappers] = FixedModifierEnum.DEFENDER_USES_SAPPERS
        buttonMap[attackerSappers] = FixedModifierEnum.ATTACKER_USES_SAPPERS
        buttonMap[attackerRecce] = FixedModifierEnum.ATTACKER_USES_REC
        buttonMap[attackerHalfEngaged] = FixedModifierEnum.ATTACKER_HALF_ENGAGED

        fun updateText() {
            val currentDifferential = calculator.calculateCurrentCombatDifferential(gameState)
            resultView.text = "Current total combat modifier: ${currentDifferential.first}"
        }

        for ((key, modifier) in buttonMap ) {
            key.setOnClickListener {
                gameState.activeFixedModifiers.map[modifier] = key.isChecked
                updateText()
            }

        }

        val adjacentAttackersField = findViewById<EditText>(R.id.adjacent_attackers)
        adjacentAttackersField.setText("0")

        Helpers.addTextFieldListener(adjacentAttackersField) {
            val count = Helpers.getIntFromTextField(adjacentAttackersField)
            gameState.adjacentAttackerCount = count
            updateText()
        }

        val adjacentDefendersField = findViewById<EditText>(R.id.adjacent_defenders)
        adjacentDefendersField.setText("0")

        Helpers.addTextFieldListener(adjacentDefendersField) {
            val count = Helpers.getIntFromTextField(adjacentDefendersField)
            gameState.adjacentDefenderCount = count
            updateText()
        }


        val applyButton = findViewById<Button>(R.id.fixed_apply)

        val intent = Intent(this, CombatSupportActivity::class.java)
        applyButton.setOnClickListener{
            //Toast.makeText(this, "Unit type: $radioButtonStr, attack strength: ${strengths.first}, defense strength: ${strengths.second}", Toast.LENGTH_LONG*10).show()
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())
            startActivity(intent)
            finish()
        }
    }


}