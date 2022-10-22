package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast

class DisengagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disengagement_selection)


        val disengagementApplyButton = findViewById<Button>(R.id.retreat_before_combat_apply)

        val noRetreatsButton = findViewById<RadioButton>(R.id.retreat_before_combat_radio_zero)
        noRetreatsButton.isChecked = true

        //val intent = Intent(this, PostureAndAttackTypeActivity::class.java)
        disengagementApplyButton.setOnClickListener{
            val radioButtonStr = checkRadioButton()
            //startActivity(intent)

            //finish()
        }
    }

    private fun checkRadioButton(): CharSequence {
        val radioGroup: RadioGroup = findViewById(R.id.retreat_before_combat_radio_group)
        val checkedId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedId)

        return checkedRadioButton.text

    }

    private fun checkSeekBars(): Pair<Int, Int> {
        val attackStrengthSeekBar: SeekBar = findViewById(R.id.attackStrengthSeekBar)
        val defenseStrengthSeekBar: SeekBar = findViewById(R.id.defenseStrengthSeekBar)

        val attackStrength = attackStrengthSeekBar.progress
        val defenseStrength = defenseStrengthSeekBar.progress

        return Pair(attackStrength, defenseStrength)
    }
}