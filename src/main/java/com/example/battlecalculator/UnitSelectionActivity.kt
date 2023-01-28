package com.example.battlecalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UnitSelectionActivity : AppCompatActivity() {

    private var selectedUnits = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection)

        val unitSelectionType = Communication.getUnitSelectionType(intent)

        val activityHeader = findViewById<TextView>(R.id.textView)
        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            activityHeader.text = "SELECT THE ATTACKING UNIT"
        } else {
            activityHeader.text = "SELECT THE DEFENDING UNIT(S)"
        }

        val gameState = getGameState(intent)

        val oob = OrderOfBattle()
        val allianceOob : OrderOfBattle.OOBData = if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            oob.getOOB(gameState.activeAlliance)
        } else {
            if (gameState.activeAlliance == Alliances.NATO) {
                oob.getOOB(Alliances.PACT)
            } else {
                oob.getOOB(Alliances.NATO)
            }
        }


        val level1RadioGroup = findViewById<RadioGroup>(R.id.level_1_radio_group)
        val level2RadioGroup = findViewById<RadioGroup>(R.id.level_2_radio_group)
        val level3RadioGroup = findViewById<RadioGroup>(R.id.level_3_radio_group)
        val level4RadioGroup = findViewById<RadioGroup>(R.id.level_4_radio_group)

        val unitAdditionBtn = findViewById<Button>(R.id.add_unit)
        val selectedView = findViewById<LinearLayout>(R.id.selected_units)
        selectedView.removeAllViews()

        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            unitAdditionBtn.visibility = View.GONE
        } else {
            unitAdditionBtn.setOnClickListener{
                val checkedBtn = findViewById<RadioButton>(level4RadioGroup.checkedRadioButtonId)
                val selectedUnit = checkedBtn.text.toString()
                if (!selectedUnits.contains(selectedUnit)) {
                    updateSelectedView(unitSelectionType, selectedUnit)
                    selectedUnits.add(selectedUnit)
                }
            }
        }

        // Init level 1
        setLevel1RadioButtons(allianceOob.level1!!,
            level1RadioGroup, level2RadioGroup, level3RadioGroup, level4RadioGroup, unitSelectionType)

        val commitButton = findViewById<Button>(R.id.combat1_apply)

        commitButton.setOnClickListener {

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                if (selectedUnits.size != 1) {
                    throw Exception("Selected units size is not 1 when committing attacking units")
                }

                val selectedUnitName = selectedUnits[0]
                val selectedUnit = gameState.oob.unitIndex[selectedUnitName]
                val unitState = UnitState(selectedUnit, null)

                gameState.attackingUnit = unitState

                val intent = Intent(this, PostureAndAttackTypeActivity::class.java)
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())
                startActivity(intent)
                finish()

            } else {
                if (selectedUnits.size > 0) {
                    for (selectedUnit in selectedUnits) {
                        val unit = gameState.oob.unitIndex[selectedUnit]
                            ?: throw Exception("Couldn't find unit with name $selectedUnit when adding defending units")
                        val unitState = UnitState(unit, null)

                        gameState.defendingUnits.add(unitState)
                    }

                    val intent = Intent(this, PostureAndAttackTypeActivity::class.java)
                    intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())
                    intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

                    startActivity(intent)
                    finish()

                }
            }




        }
    }

    private fun setLevel1RadioButtons(
        level1List: List<OrderOfBattle.Level1>,
        level1RadioGroup: RadioGroup,
        level2RadioGroup: RadioGroup,
        level3RadioGroup: RadioGroup,
        level4RadioGroup: RadioGroup,
        selectionType: UnitSelectionTypes) {

        level1RadioGroup.removeAllViews()

        var initialized = false

        for (level1 in level1List) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level1.name
            level1RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel2RadioButtons(level1, level2RadioGroup, level3RadioGroup, level4RadioGroup, selectionType)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel2RadioButtons(level1, level2RadioGroup, level3RadioGroup, level4RadioGroup, selectionType)
            }
        }
    }

    private fun setLevel2RadioButtons(
        level1: OrderOfBattle.Level1,
        level2RadioGroup: RadioGroup,
        level3RadioGroup: RadioGroup,
        level4RadioGroup: RadioGroup,
        selectionType: UnitSelectionTypes) {

        level2RadioGroup.removeAllViews()

        var initialized = false

        for (level2 in level1.level2) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level2.name
            level2RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel3RadioButtons(level2, level3RadioGroup, level4RadioGroup, selectionType)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel3RadioButtons(level2, level3RadioGroup, level4RadioGroup, selectionType)
            }
        }
    }

    private fun setLevel3RadioButtons(
        level2: OrderOfBattle.Level2,
        level3RadioGroup: RadioGroup,
        level4RadioGroup: RadioGroup,
        selectionType: UnitSelectionTypes) {

        level3RadioGroup.removeAllViews()

        var initialized = false

        for (level3 in level2.level3) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level3.name
            level3RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                setLevel4RadioButtons(level3, level4RadioGroup, selectionType)
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                setLevel4RadioButtons(level3, level4RadioGroup, selectionType)
            }
        }
    }

    private fun setLevel4RadioButtons(
        level3: OrderOfBattle.Level3, level4RadioGroup: RadioGroup, selectionType: UnitSelectionTypes) {

        level4RadioGroup.removeAllViews()

        var initialized = false

        for (level4 in level3.level4) {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = level4.name
            level4RadioGroup.addView(radioButton)

            radioButton.setOnClickListener {
                if (selectionType == UnitSelectionTypes.ATTACKER) {
                    handleLevel4ClickForAttacker(radioButton, selectionType)
                }
            }

            if (!initialized) {
                radioButton.isChecked = true
                initialized = true
                if (selectionType == UnitSelectionTypes.ATTACKER) {
                    handleLevel4ClickForAttacker(radioButton, selectionType)
                }
            }
        }
    }

    private fun handleLevel4ClickForAttacker(radioButton: RadioButton, selectionType: UnitSelectionTypes) {
        selectedUnits.clear()
        selectedUnits.add(radioButton.text.toString())
        updateSelectedView(selectionType, radioButton.text.toString())
    }

    private fun updateSelectedView(selectionType: UnitSelectionTypes, unitName : String) {
        val selectedView = findViewById<LinearLayout>(R.id.selected_units)
        val imageFileName = Images.getImageFileName(unitName)

        if (selectionType == UnitSelectionTypes.ATTACKER) {
            selectedView.removeAllViews()
            val unitImage = getUnitImage(imageFileName)
            selectedView.addView(unitImage)
        } else if (selectionType == UnitSelectionTypes.DEFENDER) {
            val unitImage = getUnitImage(imageFileName)
            selectedView.addView(unitImage)
        }

    }

    private fun getUnitImage(imageFileName : String) : ImageView {
        val unitImage = ImageView(this)
        unitImage.id = View.generateViewId()

        val unitDrawable = Images.getDrawable(imageFileName, this, applicationContext, applicationInfo)
        unitImage.setImageDrawable(unitDrawable)

        return unitImage
    }


}













