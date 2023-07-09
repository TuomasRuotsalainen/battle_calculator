package com.example.battlecalculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class UnitSelectionActivityInput : AppCompatActivity() {

    private var selectedUnits = mutableListOf<Unit>()

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


        fun handleSearch(searchTerm : String) {
            matches = oob.searchUnits(searchTerm, gameState.activeAlliance)
            // Code here executes after text has changed

            if (matches.isNotEmpty()) {
                if (selectedUnits.size > 0) {
                    val mergedList = (selectedUnits + matches).distinct()
                    updateSelectedView(mergedList, unitSelectionType)
                } else {
                    updateSelectedView(matches, unitSelectionType)

                }

            }
        }

        handleSearch("")

        unitInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Code here executes before text is changed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Code here executes while text is changing
            }

            override fun afterTextChanged(s: Editable) {
                handleSearch(s.toString())
            }
        })


        val commitButton = findViewById<Button>(R.id.combat1_apply)

        commitButton.setOnClickListener {

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                if (selectedUnits.size != 1) {
                    throw Exception("Selected units size is not 1 when committing attacking units")
                }

                //val selectedUnitName = selectedUnits[0]
                //val selectedUnit = gameState.oob.unitIndex[selectedUnitName]
                val unitState = UnitState(selectedUnits[0], null, null, null, false, 0, null, RiverCrossingTypeEnum.NONE, false)

                gameState.attackingUnit = unitState

                val intent = Intent(this, PostureAndAttackTypeActivity::class.java)
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())
                startActivity(intent)
                finish()

            } else {
                if (selectedUnits.size > 0) {
                    for (selectedUnit in selectedUnits) {
                        //val unit = gameState.oob.unitIndex[selectedUnit]
                           //?: throw Exception("Couldn't find unit with name $selectedUnit when adding defending units")
                        val unitState = UnitState(selectedUnit, null, null, null,  false, 0, null, RiverCrossingTypeEnum.NONE, false)

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

    private fun updateSelectedView(units : List<Unit>, unitSelectionType: UnitSelectionTypes) {

        val selectedView = findViewById<LinearLayout>(R.id.selected_units)
        selectedView.removeAllViews()

        val backgrounds = mutableListOf<ImageView>()

        for (unit in units) {
            val imageFileName = Images.getImageFileName(unit.name)
            val unitImage = getUnitImage(imageFileName)
            val unitImageLayout = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)

            unitImageLayout.gravity = Gravity.CENTER
            unitImage.layoutParams = unitImageLayout

            val frame = FrameLayout(this)

            //selectedView.addView(unitImage)

            val isSelected = selectedUnits.contains(unit)

            val background = ImageView(this)
            background.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            background.setImageResource(R.drawable.border_background)
            background.setColorFilter(Color.BLACK)
            background.scaleX = 1.1f
            background.scaleY = 1.1f
            background.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

            backgrounds.add(background)

            frame.addView(background)
            frame.addView(unitImage)

            selectedView.addView(frame)

            unitImage.setOnClickListener {
                Log.d("CLICKED", unit.name)
                val unitInfo = findViewById<TextView>(R.id.unitinfo)

                if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                    for (otherBackground in backgrounds) {
                        otherBackground.visibility = View.INVISIBLE
                        selectedUnits = mutableListOf()
                    }
                }

                if (background.visibility == View.VISIBLE) {
                    background.visibility = View.INVISIBLE
                    unitInfo.text = ""
                    selectedUnits.remove(unit)
                } else {
                    background.visibility = View.VISIBLE

                    unitInfo.text = unit.getString()
                    selectedUnits.add(unit)
                }
            }
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













