package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

class SupportUnitRetreatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_unit_retreat)

        val gameState = getGameState(intent)

        val riverCrossing1 = findViewById<CheckBox>(R.id.rivercorssing1)
        val riverCrossing2 = findViewById<CheckBox>(R.id.rivercorssing2)

        val column1 = findViewById<CheckBox>(R.id.movementselection_column)
        val column2 = findViewById<CheckBox>(R.id.movementselection_column2)

        val tactical1 = findViewById<CheckBox>(R.id.movementselection_tactical)
        val tactical2 = findViewById<CheckBox>(R.id.movementselection_tactical2)

        val deployed1 = findViewById<CheckBox>(R.id.movementselection_deployed)
        val deployed2 = findViewById<CheckBox>(R.id.movementselection_deployed2)

        fun toggle1(enabled : Boolean) {
            column1.isEnabled = enabled
            tactical1.isEnabled = enabled
            deployed1.isEnabled = enabled
        }

        fun toggle2(enabled : Boolean) {
            tactical2.isEnabled = enabled
            column2.isEnabled = enabled
            deployed2.isEnabled = enabled
        }

        toggle1(false)
        toggle2(false)

        fun setAtLeastOneChecked1() {
            if (!tactical1.isChecked && !column1.isChecked && !deployed1.isChecked && riverCrossing1.isChecked) {
                tactical1.isChecked = true
            }
        }

        fun setAtLeastOneChecked2() {
            if (!tactical2.isChecked && !column2.isChecked && !deployed2.isChecked && riverCrossing2.isChecked) {
                tactical2.isChecked = true
            }
        }

        riverCrossing1.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                tactical1.isChecked = false
                column1.isChecked = false
                deployed1.isChecked = false
            } else {
                setAtLeastOneChecked1()
            }

            toggle1(isChecked)
        }

        tactical1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                column1.isChecked = false
                deployed1.isChecked = false
                riverCrossing1.isChecked = true
            } else {
                setAtLeastOneChecked1()
            }

        }

        column1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tactical1.isChecked = false
                deployed1.isChecked = false
                riverCrossing1.isChecked = true
            } else {
                setAtLeastOneChecked1()
            }
        }

        deployed1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tactical1.isChecked = false
                column1.isChecked = false
                riverCrossing1.isChecked = true
            } else {
                setAtLeastOneChecked1()
            }
        }

        riverCrossing2.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                tactical2.isChecked = false
                column2.isChecked = false
                deployed2.isChecked = false
            } else {
                setAtLeastOneChecked2()
            }

            toggle2(isChecked)

        }

        tactical2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                column2.isChecked = false
                deployed2.isChecked = false
                riverCrossing2.isChecked = true
            } else {
                setAtLeastOneChecked2()
            }
        }

        column2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tactical2.isChecked = false
                deployed2.isChecked = false
                riverCrossing2.isChecked = true
            } else {
                setAtLeastOneChecked2()
            }
        }

        deployed2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tactical2.isChecked = false
                column2.isChecked = false
                riverCrossing2.isChecked = true
            } else {
                setAtLeastOneChecked2()
            }
        }


        val applyBtn = findViewById<Button>(R.id.apply)

        val movement = Movement()

        var intent : Intent
        applyBtn.setOnClickListener{

            val dice = Dice()

            val movementMode2 : MovementModeEnum = if (tactical2.isChecked) {
                MovementModeEnum.TACTICAL
            } else if (column2.isChecked) {
                MovementModeEnum.COLUM
            } else {
                MovementModeEnum.DEPLOYED
            }

            var resultText = ""

            if (riverCrossing1.isChecked) {
                val diceResult1 = dice.roll()


                val movementMode1 : MovementModeEnum = if (tactical1.isChecked) {
                    MovementModeEnum.TACTICAL
                } else if (column1.isChecked) {
                    MovementModeEnum.COLUM
                } else {
                    MovementModeEnum.DEPLOYED
                }

                val attrition1 = movement.getAttritionForHastyCrossingDuringRetreat(diceResult1, movementMode1)

                resultText += "First support unit: River crossing dice roll: ${diceResult1.get()}. Result: $attrition1 attrition.\n\n"

            }

            if (riverCrossing2.isChecked) {
                val diceResult2 = dice.roll()


                val movementMode2 : MovementModeEnum = if (tactical2.isChecked) {
                    MovementModeEnum.TACTICAL
                } else if (column2.isChecked) {
                    MovementModeEnum.COLUM
                } else {
                    MovementModeEnum.DEPLOYED
                }

                val attrition2 = movement.getAttritionForHastyCrossingDuringRetreat(diceResult2, movementMode2)

                resultText += "Second support unit: River crossing dice roll: ${diceResult2.get()}. Result: $attrition2 attrition.\n\n"

            }

            if (resultText == "") {
                resultText = "No retreats over minor river. All support units can retreat normally to available hexes."
            }

            Helpers.showInfoDialog(
                this,
                resultText,
                "Understood",
                null,
                {
                    // No disengagements
                    intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                    startActivity(intent)
                    finish()
                })

        }
    }

}