package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast

class UnitSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection)

        // TODO get this properly
        val oob = OrderOfBattle().getOOB(Alliances.NATO)

        val level1RadioGroup = findViewById<RadioGroup>(R.id.level_1_radio_group)

        setLevel1RadioButtons(oob.level1!!, level1RadioGroup)


        //setLevel2RadioButtons

        /*
        for (level1 in oob.level1!!) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level1.name
            level1RadioGroup.addView(radioButton)
        }*/



        /*
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
        }*/
    }

    private fun setLevel1RadioButtons(level1List: List<OrderOfBattle.Level1>, level1RadioGroup: RadioGroup) {
        level1RadioGroup.removeAllViews()

        var initialized = false

        for (level1 in level1List) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level1.name
            level1RadioGroup.addView(radioButton)

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
            }
        }
    }

    private fun setLevel2RadioButtons(level2List: List<OrderOfBattle.Level2>, level1RadioGroup: RadioGroup) {
        level1RadioGroup.removeAllViews()

        var initialized = false

        for (level2 in level2List) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level2.name
            level1RadioGroup.addView(radioButton)

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
            }
        }
    }
}