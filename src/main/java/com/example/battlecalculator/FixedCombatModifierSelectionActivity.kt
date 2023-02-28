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
        val attackerAdvanceAxisView = findViewById<CheckBox>(R.id.attackerAdvanceAxis)
        val defenderAdvanceAxisView = findViewById<CheckBox>(R.id.defenderAdvanceAxis)
        val defenderResting = findViewById<CheckBox>(R.id.defenderResting)
        val defenderDelayed = findViewById<CheckBox>(R.id.defenderDelayed)

        val resultView = findViewById<TextView>(R.id.resultView)

        if (gameState.activeAlliance == Alliances.NATO) {
            // Add nato option to defender
            defenderOptions.removeView(natoDefendView)
            attackerOptions.removeView(attackerAdvanceAxisView)
        } else {
            defenderOptions.removeView(defenderAdvanceAxisView)
        }

        val buttonMap : HashMap<CheckBox, FixedModifierEnum> = HashMap()
        buttonMap[defenderResting] = FixedModifierEnum.DEFENDER_RESTING
        buttonMap[defenderDelayed] = FixedModifierEnum.DEFENDER_DELAYED
        buttonMap[attackerAdvanceAxisView] = FixedModifierEnum.PACT_ATTACKING_REAR
        buttonMap[defenderAdvanceAxisView] = FixedModifierEnum.PACT_DEFENDING_REAR
        buttonMap[natoDefendView] = FixedModifierEnum.NATO_DEFENDS_MULTI_COUNTRY

        for ((key, modifier) in buttonMap ) {
            key.setOnClickListener {
                gameState.activeFixedModifiers.map[modifier] = key.isChecked
                val currentDifferential = calculator.calculateCurrentCombatDifferential(gameState)
                resultView.text = "Current total combat modifier: $currentDifferential"
            }

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