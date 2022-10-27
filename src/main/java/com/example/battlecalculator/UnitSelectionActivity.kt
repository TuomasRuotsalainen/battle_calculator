package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.battlecalculator.Utils.IntentTools.getStringFromIntent

class UnitSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection)

        val gameStateString = getStringFromIntent(intent, IntentExtraIDs.GAMESTATE.toString())
        val unitSelectionType = getStringFromIntent(intent, IntentExtraIDs.UNITSELECTIONTYPE.toString())

        val gameState = GameState(gameStateString)

        // TODO get this properly
        val oob = OrderOfBattle()
        val allianceOob = oob.getOOB(Alliances.NATO)

        val level1RadioGroup = findViewById<RadioGroup>(R.id.level_1_radio_group)
        val level2RadioGroup = findViewById<RadioGroup>(R.id.level_2_radio_group)
        val level3RadioGroup = findViewById<RadioGroup>(R.id.level_3_radio_group)
        val level4RadioGroup = findViewById<RadioGroup>(R.id.level_4_radio_group)

        // Init level 1
        setLevel1RadioButtons(allianceOob.level1!!,
            level1RadioGroup, level2RadioGroup, level3RadioGroup, level4RadioGroup)

        val commitButton = findViewById<Button>(R.id.retreat_before_combat_apply)

        val intent = Intent(this, UnitSelectionActivity::class.java)

        commitButton.setOnClickListener {
            val checkedLevel4ButtonId = level4RadioGroup.checkedRadioButtonId
            val checkedButton = findViewById<Button>(checkedLevel4ButtonId)

            val selectedUnitName = checkedButton.text.toString()

            val selectedUnit = gameState.oob.unitIndex[selectedUnitName]

            if (unitSelectionType == UnitSelectionTypes.ATTACKER.toString()) {
                gameState.attackingUnit = selectedUnit
                gameState.getStateString()
            } else {
                // Unit selection type defender
                throw Exception("Not implemented")
            }

            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())

            startActivity(intent)
            finish()
        }
    }

    private fun setLevel1RadioButtons(
        level1List: List<OrderOfBattle.Level1>,
        level1RadioGroup: RadioGroup,
        level2RadioGroup: RadioGroup,
        level3RadioGroup: RadioGroup,
        level4RadioGroup: RadioGroup) {

        level1RadioGroup.removeAllViews()

        var initialized = false

        for (level1 in level1List) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level1.name
            level1RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel2RadioButtons(level1, level2RadioGroup, level3RadioGroup, level4RadioGroup)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel2RadioButtons(level1, level2RadioGroup, level3RadioGroup, level4RadioGroup)
            }
        }
    }

    private fun setLevel2RadioButtons(
        level1: OrderOfBattle.Level1,
        level2RadioGroup: RadioGroup,
        level3RadioGroup: RadioGroup,
        level4RadioGroup: RadioGroup) {

        level2RadioGroup.removeAllViews()

        var initialized = false

        for (level2 in level1.level2) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level2.name
            level2RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel3RadioButtons(level2, level3RadioGroup, level4RadioGroup)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel3RadioButtons(level2, level3RadioGroup, level4RadioGroup)
            }
        }
    }

    private fun setLevel3RadioButtons(level2: OrderOfBattle.Level2, level3RadioGroup: RadioGroup, level4RadioGroup: RadioGroup) {
        level3RadioGroup.removeAllViews()

        var initialized = false

        for (level3 in level2.level3) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level3.name
            level3RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel4RadioButtons(level3, level4RadioGroup)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel4RadioButtons(level3, level4RadioGroup)
            }
        }
    }

    private fun setLevel4RadioButtons(level3: OrderOfBattle.Level3, level4RadioGroup: RadioGroup) {
        level4RadioGroup.removeAllViews()

        var initialized = false

        for (level4 in level3.level4) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level4.name
            level4RadioGroup.addView(radioButton)

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
            }
        }
    }
}