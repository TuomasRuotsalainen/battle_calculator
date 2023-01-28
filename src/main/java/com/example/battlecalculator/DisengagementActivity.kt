package com.example.battlecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar

class DisengagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disengagement_selection)

        var gameState = getGameState(intent)
        Log.d("TUOMAS STATE", gameState.getStateString())

        val disengagementApplyButton = findViewById<Button>(R.id.combat1_apply)

        val noRetreatsButton = findViewById<RadioButton>(R.id.retreat_before_combat_radio_zero)
        noRetreatsButton.isChecked = true



        //val intent = Intent(this, PostureAndAttackTypeActivity::class.java)
        disengagementApplyButton.setOnClickListener{
            val radioButton = checkRadioButton()
            if (radioButton == R.id.retreat_before_combat_radio_zero) {
                // No retreats, go to combat
            } else if (radioButton == R.id.retreat_before_combat_radio_one) {
                // One retreat
            } else {
                // Two retreats
            }
            //startActivity(intent)

            //finish()
        }
    }

    private fun checkRadioButton(): Int {
        val radioGroup: RadioGroup = findViewById(R.id.retreat_before_combat_radio_group)
        val checkedId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedId)

        return checkedRadioButton.id

    }

    private fun checkSeekBars(): Pair<Int, Int> {
        val attackStrengthSeekBar: SeekBar = findViewById(R.id.attackStrengthSeekBar)
        val defenseStrengthSeekBar: SeekBar = findViewById(R.id.defenseStrengthSeekBar)

        val attackStrength = attackStrengthSeekBar.progress
        val defenseStrength = defenseStrengthSeekBar.progress

        return Pair(attackStrength, defenseStrength)
    }
}