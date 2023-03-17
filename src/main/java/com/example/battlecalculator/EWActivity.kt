package com.example.battlecalculator

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlin.Unit

class EWActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electronic_warfare)

        var gameState = getGameState(intent)

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }

        // Display starting stats, prompt to generate EW results
        val STAGE_PROMPT_EW = "STAGE_PROMPT_EW"

        // Display stats with EW results applied, prompt to calculate total combat support
        val STAGE_DISPLAY_EW_PROMPT_CS = "STAGE_DISPLAY_EW_PROMPT_CS"

        // Display starting stats, prompt to calculate total combat support
        val STAGE_PROMPT_CS = "STAGE_PROMPT_CS"

        // Display total combat support results
        val STAGE_DISPLAY_CS_PROMPT_BATTLE = "STAGE_DISPLAY_CS_PROMPT_BATTLE"

        // Display final combat results!
        val STAGE_DISPLAY_BATTLE_RESULTS = "STAGE_DISPLAY_BATTLE_RESULTS"

        class Stage() {
            private var current : String = STAGE_PROMPT_EW
            fun get() : String {
                return current
            }

            fun proceed() {
                Log.d("DEBUG", "Proceeding stage. Current one: $current")
                current = when (current) {
                    STAGE_PROMPT_EW -> STAGE_DISPLAY_EW_PROMPT_CS
                    STAGE_DISPLAY_EW_PROMPT_CS -> STAGE_DISPLAY_CS_PROMPT_BATTLE
                    STAGE_PROMPT_CS -> STAGE_DISPLAY_CS_PROMPT_BATTLE
                    STAGE_DISPLAY_CS_PROMPT_BATTLE -> STAGE_DISPLAY_BATTLE_RESULTS
                    else -> throw Exception("Unknown stage")
                }

                Log.d("DEBUG", "New one: $current")
            }

            fun noEwNeeded() {
                current = STAGE_PROMPT_CS
            }
        }

        val attackerEWPoints = gameState.combatSupport!!.getAttackerCombatSupport()!!.getEWPoints()
        val attackerEWModifier = gameState.combatSupport!!.getAttackerCombatSupport()!!.getEWRollModifier()

        val defenderEWPoints = gameState.combatSupport!!.getDefenderCombatSupport()!!.getEWPoints()
        val defenderEWModifier = gameState.combatSupport!!.getDefenderCombatSupport()!!.getEWRollModifier()

        val ewTable = Tables.EW()

        //val ewResultsNeeded = gameState.combatSupport!!.ewInUse()
        //var ewResultsCompleted = false

        var attackerEwResult : Tables.EWResult? = null
        var defenderEwResult : Tables.EWResult? = null

        var groundCombatResult : Tables.GroundCombatResult? = null

        val defenderRoll = DieRoll()
        val attackerRoll = DieRoll()

        val defenderCsRoll = DieRoll()
        val attackerCsRoll = DieRoll()

        val combatRoll = DieRoll()

        val stage = Stage()
        val stage2Apply = "Calculate and apply final combat support"
        val applyButton = findViewById<Button>(R.id.apply)

        if(!gameState.combatSupport!!.ewInUse()) {
            stage.noEwNeeded()
            applyButton.text = stage2Apply
        } else {
            attackerEwResult = ewTable.getResultForModifiedRoll(attackerRoll.getResultWithoutModifiers() + attackerEWModifier!!, attackerEWPoints!!)
            defenderEwResult = ewTable.getResultForModifiedRoll(defenderRoll.getResultWithoutModifiers() + defenderEWModifier!!, defenderEWPoints!!)
        }


        val textBody = findViewById<TextView>(R.id.body)

        val calculator = Calculator()

        var attackerCs : Int? = null
        var defenderCs : Int? = null

        fun updateTextBody(stage : Stage) {
            Log.d("DEBUG", "Updating text body. Current stage: ${stage.get()}")
            val currentDifferential = calculator.calculateCurrentCombatDifferential(gameState)
            val attackerCombatSupport = gameState.combatSupport!!.getAttackerCombatSupport()
            val defenderCombatSupport = gameState.combatSupport!!.getDefenderCombatSupport()

            var text = ""
            val combatDifAfterAA = "Current combat differential\n$currentDifferential\n\n Combat support allocation after AA fire (attacker:defender)\n${attackerCombatSupport!!.getTotalSupport()}:${defenderCombatSupport!!.getTotalSupport()}\n\n"

            if (stage.get() == STAGE_PROMPT_EW) {
                text += combatDifAfterAA
                text += "EW points allocation (attacker:defender) ${attackerCombatSupport.getEWPoints()}:${defenderCombatSupport.getEWPoints()}"
            } else if (stage.get() == STAGE_DISPLAY_EW_PROMPT_CS) {
                text += "EW roll results: Attacker: ${attackerRoll.getResultWithoutModifiers()}, Defender: ${defenderRoll.getResultWithoutModifiers()}\n\n"
                text += "Attacker EW effects: ${attackerEwResult!!.getResultAsText(true)}\nDefender EW effects: ${defenderEwResult!!.getResultAsText(false)}"
            } else if (stage.get() == STAGE_PROMPT_CS) {
                text += combatDifAfterAA
                // No special actions needed here
            } else if (stage.get() == STAGE_DISPLAY_CS_PROMPT_BATTLE) {
                text =
                    "Attacker combat support die: ${attackerCsRoll.getResultWithoutModifiers()}. Final combat support modifier: $attackerCs\n"
                text += "Defender combat support die: ${defenderCsRoll.getResultWithoutModifiers()}. Final combat support modifier: $defenderCs\n\n"
                text += "Final combat differential: ${
                    calculator.calculateCurrentCombatDifferential(
                        gameState
                    ) + attackerCs!! + defenderCs!!
                }"
            } else if (stage.get() == STAGE_DISPLAY_BATTLE_RESULTS) {
                text += "Ground combat die roll: ${combatRoll.getResultWithoutModifiers()}\n\nCombat result:\n"
                text += "Attrition to attacker: ${groundCombatResult!!.attackerAttrition}\n" +
                        "Attrition to defender: ${groundCombatResult!!.attackerAttrition}\n"
                if (gameState.areSappersHit(groundCombatResult!!)) {
                    text += "Sappers eliminated"
                }
            } else {
                throw Exception("Unrecognized stage ${stage.get()}")
            }

            textBody.text = text
        }

        updateTextBody(stage)

        val explainBtn = findViewById<Button>(R.id.explain)
        explainBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("""Current combat modifiers:
                |TODO""".trimMargin())
            builder.setPositiveButton("Understood") { dialog, _ ->
                // Close the popup
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        val combatSupportCalc = Tables.CombatSupport()

        applyButton.setOnClickListener {
            Log.d("DEBUG", "Button clicked! Stage: ${stage.get()}")
            if (stage.get() == STAGE_PROMPT_EW) {
                gameState.activeFixedModifiers.applyEW(attackerEwResult!!, defenderEwResult!!)
                gameState.combatSupport!!.applyEwResults(attackerEwResult, defenderEwResult)

                applyButton.text = stage2Apply

            } else if (stage.get() == STAGE_PROMPT_CS || stage.get() == STAGE_DISPLAY_EW_PROMPT_CS) {
                attackerCs = combatSupportCalc.calculateForAttacker(gameState.combatSupport!!.getAttackerCombatSupport()!!.getTotalSupport(), attackerCsRoll)
                defenderCs = combatSupportCalc.calculateForDefender(gameState.combatSupport!!.getDefenderCombatSupport()!!.getTotalSupport(), defenderCsRoll)

                applyButton.text = "Calculate combat results"

            } else if (stage.get() == STAGE_DISPLAY_CS_PROMPT_BATTLE) {
                val groundCombat = Tables.GroundCombat()
                groundCombatResult = groundCombat.getResult(combatRoll, calculator.calculateCurrentCombatDifferential(gameState) + attackerCs!! + defenderCs!!)

                applyButton.text = "TODO what happens now?"
            }

            stage.proceed()
            updateTextBody(stage)

        }

    }



}