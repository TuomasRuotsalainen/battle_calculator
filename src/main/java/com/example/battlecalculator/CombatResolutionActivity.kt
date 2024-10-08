package com.example.battlecalculator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.util.Log
import com.example.battlecalculator.Helpers.General.showInfoDialog

class CombatResolutionActivity : AppCompatActivity() {

    // TODO Calculate and apply final combat modifiers still crash the app occasionally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat_resolution)

        try {

        val gameState = getGameState(intent)

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

        // Display going to next ativity
        val STAGE_NEXT_ACTIVITY = "STAGE_NEXT_ACTIVITY"

        class Stage() {
            private var current : String = STAGE_PROMPT_EW
            fun get() : String {
                return current
            }

            fun proceed() {
                current = when (current) {
                    STAGE_PROMPT_EW -> STAGE_DISPLAY_EW_PROMPT_CS
                    STAGE_DISPLAY_EW_PROMPT_CS -> STAGE_DISPLAY_CS_PROMPT_BATTLE
                    STAGE_PROMPT_CS -> STAGE_DISPLAY_CS_PROMPT_BATTLE
                    STAGE_DISPLAY_CS_PROMPT_BATTLE -> STAGE_DISPLAY_BATTLE_RESULTS
                    STAGE_DISPLAY_BATTLE_RESULTS -> STAGE_NEXT_ACTIVITY
                    else -> throw Exception("Unknown stage")
                }

            }

            fun noEwNeeded() {
                current = STAGE_PROMPT_CS
            }
        }

        var errorMessage = ""

        val attackerSupport = gameState.combatSupport!!.getAttackerCombatSupport()!!
        val defenderSupport = gameState.combatSupport!!.getDefenderCombatSupport()!!

        var attackerEWPoints = 0
        var attackerEWModifier = 0

        var defenderEWPoints = 0
        var defenderEWModifier = 0

        if (attackerSupport.getEWPoints() == null) {
            errorMessage = "Attacker ew points is null"
        } else if (attackerSupport.getEWRollModifier() == null) {
            errorMessage = "Attacker ew roll modifier is null"
        } else if (defenderSupport.getEWPoints() == null) {
            errorMessage = "Defender ew points is null"
        } else if (defenderSupport.getEWRollModifier() == null) {
            errorMessage = "Defender ew roll modifier is null"
        }

        if (errorMessage != "") {
            showInfoDialog(this,
                "$errorMessage. Combat result calculation will be incorrect.", "Understood",null, {

            })
        } else {
            attackerEWPoints = attackerSupport.getEWPoints()!!
            attackerEWModifier = attackerSupport.getEWRollModifier()!!

            defenderEWPoints = defenderSupport.getEWPoints()!!
            defenderEWModifier = defenderSupport.getEWRollModifier()!!
        }


        val ewTable = Tables.EW()

        var attackerEwResult : Tables.EWResult? = null
        var defenderEwResult : Tables.EWResult? = null

        var groundCombatResult : Tables.GroundCombatResult? = null

        val dice = Dice()
        val defenderRoll = dice.roll()
        val attackerRoll = dice.roll()

        val defenderCsRoll = dice.roll()
        val attackerCsRoll = dice.roll()

        val combatRoll = dice.roll()

        val stage = Stage()
        val stage2Apply = "Calculate and apply final combat support"
        val applyButton = findViewById<Button>(R.id.apply)
        val retreatRuleBtn = findViewById<Button>(R.id.explainretreat)

        retreatRuleBtn.setOnClickListener {
            showInfoDialog(this, Movement().getRetreatPrerequisites(), "Understood",null, {})
        }

        if(!gameState.combatSupport!!.ewInUse()) {
            stage.noEwNeeded()
            applyButton.text = stage2Apply
        } else {
            attackerEwResult = ewTable.getResultForModifiedRoll(attackerRoll.get() + attackerEWModifier!!, attackerEWPoints)
            defenderEwResult = ewTable.getResultForModifiedRoll(defenderRoll.get() + defenderEWModifier!!, defenderEWPoints)
        }


        val textBody = findViewById<TextView>(R.id.body)

        val calculator = Calculator()

        var attackerCs : Int? = null
        var defenderCs : Int? = null

        val btnLayout = findViewById<LinearLayout>(R.id.button_container)

        val noRetreatBtn = findViewById<Button>(R.id.no_retreat)

        var defenderSufferedAttrition = false

        btnLayout.removeViewAt(1)

        var finalResults : String? = null

        fun updateTextBody(stage : Stage) {

            val currentDifferential : Int = calculator.calculateCurrentCombatDifferential(gameState).first
            val attackerCombatSupport = gameState.combatSupport!!.getAttackerCombatSupport()
            val defenderCombatSupport = gameState.combatSupport!!.getDefenderCombatSupport()

            var text = ""
            val combatDifAfterAA = "Current combat differential\n$currentDifferential\n\n Combat support allocation after AA fire (attacker:defender)\n${attackerCombatSupport!!.getTotalSupport()}:${defenderCombatSupport!!.getTotalSupport()}\n\n"

            if (stage.get() == STAGE_PROMPT_EW) {
                text += combatDifAfterAA
                text += "EW points allocation (attacker:defender) ${attackerCombatSupport.getEWPoints()}:${defenderCombatSupport.getEWPoints()}"
            } else if (stage.get() == STAGE_DISPLAY_EW_PROMPT_CS) {
                text += "EW roll results: Attacker: ${attackerRoll.get()}, Defender: ${defenderRoll.get()}\n\n"
                text += "Attacker EW effects: ${attackerEwResult!!.getResultAsText(true)}\nDefender EW effects: ${defenderEwResult!!.getResultAsText(false)}"
            } else if (stage.get() == STAGE_PROMPT_CS) {
                text += combatDifAfterAA
                // No special actions needed here
            } else if (stage.get() == STAGE_DISPLAY_CS_PROMPT_BATTLE) {
                text =
                    "Attacker combat support die: ${attackerCsRoll.get()}. Final combat support modifier: $attackerCs\n"
                text += "Defender combat support die: ${defenderCsRoll.get()}. Final combat support modifier: $defenderCs\n\n"
                text += "Final combat differential: ${
                    calculator.calculateCurrentCombatDifferential(gameState).first + attackerCs!! + defenderCs!!
                }"
            } else if (stage.get() == STAGE_DISPLAY_BATTLE_RESULTS) {
                val possibleFullAssaultAttrition = if (gameState.attackingUnit!!.inFullAssault() && groundCombatResult!!.attackerAttrition > 0) {
                    "(One additional attrition point to attacker in full assault posture)\n"
                } else {
                    ""
                }

                finalResults = "Combat roll: ${combatRoll.get()}\n\n"
                finalResults += "Attrition to the attacking unit: ${groundCombatResult!!.attackerAttrition}\n" + possibleFullAssaultAttrition +
                        "Attrition to all defending units: ${groundCombatResult!!.defenderAttrition}\n"
                if (groundCombatResult!!.defenderAttrition > 0 && gameState.hexTerrain!!.getDefensiveWorksCombatModifier() < 0) {
                    // 31.4.1. Reducing defensive works
                    finalResults += "Defensive works reduced by one level.\n"
                }
                if (gameState.areSappersHit(groundCombatResult!!)) {
                    finalResults += "Sappers eliminated"
                }
                text += finalResults

                if (groundCombatResult!!.defenderAttrition > 0) {
                    defenderSufferedAttrition = true
                }
            } else if (stage.get() == STAGE_NEXT_ACTIVITY) {
                text = "This shouldn't show"
            } else {
                throw Exception("Unrecognized stage ${stage.get()}")
            }

            textBody.text = text
        }

        updateTextBody(stage)

        val explainBtn = findViewById<Button>(R.id.explain)
        explainBtn.setOnClickListener {
            val differential = calculator.calculateCurrentCombatDifferential(gameState)
            var message = differential.second


            val builder = AlertDialog.Builder(this)
            if (attackerCs != null && defenderCs != null) {
                message += "Attacker combat support: $attackerCs, defender combat support: $defenderCs"
            }

            builder.setMessage(message)
            builder.setPositiveButton("Understood") { dialog, _ ->
                // Close the popup
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        val combatSupportCalc = Tables.CombatSupport()

        fun addExitButton() {
            btnLayout.addView(noRetreatBtn)
            noRetreatBtn.setOnClickListener {
                var dialogText = "Apply the following results now:\n\n" + finalResults!!
                if (defenderSufferedAttrition) {
                    dialogText += "\n\n If every defending unit is destroyed, the rest at the hex have to retreat."
                    dialogText += "\n\nMark all defending units as ENGAGED"
                } else {
                    dialogText += "\n\nMark all defending units at least as HALF-ENGAGED"
                }
                showInfoDialog(this, dialogText, "Understood",null, {
                    val freshGameState = GameState(gameState.conditions)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), freshGameState.getStateString())
                    startActivity(intent)
                    finish()
                })


            }
        }

        applyButton.setOnClickListener {
            if (stage.get() == STAGE_PROMPT_EW) {
                gameState.activeFixedModifiers.applyEW(attackerEwResult!!, defenderEwResult!!, gameState)
                gameState.combatSupport!!.applyEwResults(attackerEwResult, defenderEwResult)

                applyButton.text = stage2Apply

            } else if (stage.get() == STAGE_PROMPT_CS || stage.get() == STAGE_DISPLAY_EW_PROMPT_CS) {
                attackerCs = combatSupportCalc.calculateForAttacker(gameState.combatSupport!!.getAttackerCombatSupport()!!.getTotalSupport(), attackerCsRoll)
                defenderCs = combatSupportCalc.calculateForDefender(gameState.combatSupport!!.getDefenderCombatSupport()!!.getTotalSupport(), defenderCsRoll)

                applyButton.text = "Calculate combat results"

            } else if (stage.get() == STAGE_DISPLAY_CS_PROMPT_BATTLE) {
                val groundCombat = Tables.GroundCombat()
                groundCombatResult = groundCombat.getResult(
                    combatRoll,
                    calculator.calculateCurrentCombatDifferential(gameState).first + attackerCs!! + defenderCs!!,
                    gameState.attackingUnit!!.inFullAssault()
                    )

                //val retreatText = Movement().getRetreatPrerequisites()
                applyButton.text = "Attempt to\n retreat"

                addExitButton()
            } else if (stage.get() == STAGE_DISPLAY_BATTLE_RESULTS) {

                val newUnits : MutableList<UnitState> = mutableListOf()
                for (unit in gameState.defendingUnits) {
                    unit.orderDisengagementAttempt()

                    newUnits.add(unit)
                }

                gameState.defendingUnits = newUnits

                val intent = Intent(this, DisengagementActivity::class.java)
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())
                startActivity(intent)
                finish()
            }

            stage.proceed()
            updateTextBody(stage)

        }

    } catch (e: Throwable) {

        val msg = e.toString()
        showInfoDialog(this, msg, "Understood",null, {})

        Log.d("ERROR", msg)

        }

    }



}