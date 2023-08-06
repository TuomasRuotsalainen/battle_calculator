package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

class RetreatBeforeCombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retreat_before_combat)

        val gameState = getGameState(intent)

        val disengagementApplyButton = findViewById<Button>(R.id.combat1_apply)

        val noRetreatsButton = findViewById<CheckBox>(R.id.retreat_before_combat_radio_zero)
        noRetreatsButton.isChecked = true

        val unit1Checkbox = findViewById<CheckBox>(R.id.retreating_unit_1_radio)
        val unit1Layout = findViewById<LinearLayout>(R.id.retreating_unit_1)
        val unit1Image = findViewById<ImageView>(R.id.retreating_unit_1_image)
        val unit1Posture = findViewById<ImageView>(R.id.retreating_unit_1_posture)

        val unit2Checkbox = findViewById<CheckBox>(R.id.retreating_unit_2_radio)
        val unit2Layout = findViewById<LinearLayout>(R.id.retreating_unit_2)
        val unit2Image = findViewById<ImageView>(R.id.retreating_unit_2_image)
        val unit2Posture = findViewById<ImageView>(R.id.retreating_unit_2_posture)

        val defendingUnits = gameState.defendingUnits

        if (defendingUnits.size == 1) {
            unit2Layout.removeAllViews()
            Images.setImageViewForUnit(unit1Image, defendingUnits[0], false, this, applicationContext)
            Images.setImageViewForUnit(unit1Posture, defendingUnits[0], true, this, applicationContext)
        } else {
            Images.setImageViewForUnit(unit1Image, defendingUnits[0], false, this, applicationContext)
            Images.setImageViewForUnit(unit1Posture, defendingUnits[0], true, this, applicationContext)

            Images.setImageViewForUnit(unit2Image, defendingUnits[1], false, this, applicationContext)
            Images.setImageViewForUnit(unit2Posture, defendingUnits[1], true, this, applicationContext)
        }


        unit2Checkbox.setOnClickListener {
            noRetreatsButton.isChecked = false
        }

        unit1Checkbox.setOnClickListener {
            noRetreatsButton.isChecked = false
        }

        noRetreatsButton.setOnClickListener {
            unit1Checkbox.isChecked = false
            unit2Checkbox.isChecked = false
        }

        var intent : Intent
        disengagementApplyButton.setOnClickListener{
            if (!noRetreatsButton.isChecked) {
                if (unit1Checkbox.isChecked) {
                    defendingUnits[0].orderDisengagementAttempt()
                }

                if (unit2Checkbox.isChecked) {
                    defendingUnits[1].orderDisengagementAttempt()
                }

                gameState.defendingUnits = mutableListOf(defendingUnits[0])

                if (defendingUnits.size == 2) {
                    gameState.defendingUnits.add(defendingUnits[1])
                }

            }

            // No disengagements
            intent = Intent(this, TargetTerrainSelectionActivity::class.java)
            intent.putExtra(
                IntentExtraIDs.UNITSELECTIONTYPE.toString(),
                UnitSelectionTypes.DEFENDER.toString()
            )
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            startActivity(intent)
            finish()

        }
    }

}