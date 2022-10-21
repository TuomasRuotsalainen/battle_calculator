package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast

class GroundCombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groundcombat)


        val groundCombatApplyButton = findViewById<Button>(R.id.groudcombatApply)

        //val intent = Intent(this, GroundCombatActivity::class.java)
        groundCombatApplyButton.setOnClickListener{
            val radioButtonStr = checkRadioButton()
            val strengths = checkSeekBars()

            Toast.makeText(this, "Unit type: $radioButtonStr, attack strength: ${strengths.first}, defense strength: ${strengths.second}", Toast.LENGTH_LONG*10).show()

            //finish()
        }
    }

    fun checkRadioButton(): CharSequence {
        val radioGroup: RadioGroup = findViewById<RadioGroup>(R.id.unitTypeRadio)
        val checkedId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedId)

        return checkedRadioButton.text

    }

    fun checkSeekBars(): Pair<Int, Int> {
        val attackStrengthSeekBar: SeekBar = findViewById(R.id.attackStrengthSeekBar)
        val defenseStrengthSeekBar: SeekBar = findViewById(R.id.defenseStrengthSeekBar)

        val attackStrength = attackStrengthSeekBar.progress
        val defenseStrength = defenseStrengthSeekBar.progress

        return Pair(attackStrength, defenseStrength)
    }
}