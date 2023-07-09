package com.example.battlecalculator

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class UnitSelectionActivityInput : AppCompatActivity() {

    private var selectedUnits = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_selection_text_input)

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



        val unitInput = findViewById<TextInputEditText>(R.id.unitInputEdit)

        var matches : List<Unit> = listOf<Unit>()




        unitInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Code here executes before text is changed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Code here executes while text is changing
            }

            override fun afterTextChanged(s: Editable) {
                Log.d("TUOMAS TEST", s.toString())
                matches = oob.searchUnits(s.toString(), gameState.activeAlliance)
                // Code here executes after text has changed

                if (matches.isNotEmpty()) {
                    updateSelectedView(unitSelectionType, matches[0].name)

                }
            }
        })


        val commitButton = findViewById<Button>(R.id.combat1_apply)

        commitButton.setOnClickListener {

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                if (selectedUnits.size != 1) {
                    throw Exception("Selected units size is not 1 when committing attacking units")
                }

                val selectedUnitName = selectedUnits[0]
                val selectedUnit = gameState.oob.unitIndex[selectedUnitName]
                val unitState = UnitState(selectedUnit, null, null, null, false, 0, null, RiverCrossingTypeEnum.NONE, false)

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
                        val unitState = UnitState(unit, null, null, null,  false, 0, null, RiverCrossingTypeEnum.NONE, false)

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

    private fun updateSelectedView(selectionType: UnitSelectionTypes, unitName : String) {
        val selectedView = findViewById<LinearLayout>(R.id.selected_units)
        Log.d("TUOAMS TEST 1", unitName)
        val imageFileName = Images.getImageFileName(unitName)

        Log.d("TUOAMS TEST 2", imageFileName)
        if (selectionType == UnitSelectionTypes.ATTACKER) {
            selectedView.removeAllViews()
            val unitImage = getUnitImage(imageFileName)
            Log.d("TUOAMS TEST 3", unitImage.toString())
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













