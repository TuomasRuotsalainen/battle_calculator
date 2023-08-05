package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import kotlin.Unit

class AAFireActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aa_fire)

        var gameState = getGameState(intent)
        if (gameState.attackingUnit == null) {
            throw Exception("Attacking unit is null")
        } else if (gameState.attackingUnit!!.unit == null) {
            throw Exception("Attacking unit unit is null")
        }

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }

        class AASettingRow(private val layout : LinearLayout) {
            private val textView : TextView = layout.getChildAt(0) as TextView
            private val editText : EditText = layout.getChildAt(1) as EditText
            private val rotaryCheckBox : CheckBox? = getCheckbox(layout)

            fun setResult(text : String, enableCheckBox : Boolean) {
                textView.text = text

                // remove EditText view
                this.layout.removeViewAt(1)

                if (enableCheckBox) {
                    enableCheckBox()
                }
            }

            fun getValue() : Int {
                return Helpers.getIntFromTextField(editText)
            }

            fun enableCheckBox() {
                this.layout.addView(this.rotaryCheckBox)
            }

            fun isRotaryDestroyed() : Boolean {
                return rotaryCheckBox!!.isChecked
            }

            fun removeCheckBox() {
                layout.removeViewAt(2)
            }

            private fun getCheckbox(layout : LinearLayout) : CheckBox? {
                if (layout.childCount > 2) {
                    return layout.getChildAt(2) as CheckBox
                }

                return null
            }
        }

        class AASettings(private val mainLayout : LinearLayout, private val combatSupport: CombatSupport, private val name : String) {
            private val fixed : AASettingRow? = createFixedRow()
            private val rotary : List<AASettingRow> = createRotaryRows()

            private var fixedResult : Tables.AAFire.Result? = null
            private var rotaryResult : List<Tables.AAFire.Result> = listOf<Tables.AAFire.Result>()

            fun fixedInUse() : Boolean {
                return (fixed != null)
            }



            fun rotaryInUse() : Boolean {
                return rotary.isNotEmpty()
            }

            fun getAaAgainstFixed(): Int? {
                if (fixed == null) {
                    return null
                }

                return fixed.getValue()
            }

            fun getDestroyedRotariesIndexes(): List<Int> {
                val destroyedList = mutableListOf<Int>()
                var idx = 0
                for (rotaryRow in rotary) {
                    if (rotaryRow.isRotaryDestroyed()) {
                        destroyedList.add(idx)
                    }

                    idx += 1
                }

                return destroyedList
            }

            fun getAaAgainstRotary(): List<Int> {
                val list = mutableListOf<Int>()
                for (row in rotary) {
                    list.add(row.getValue())
                }

                return list
            }

            fun getFixedResult(): Tables.AAFire.Result? {
                return fixedResult
            }

            fun getRotaryResult(): List<Tables.AAFire.Result> {
                return getRotaryResult()
            }


            fun setResults(fixedResult : Tables.AAFire.Result?, rotaryResults : List<Tables.AAFire.Result>) {
                if (fixedInUse()) {
                    if (fixedResult == null) {
                        throw Exception("No results for AA against fixed wing, but fixed wing in use")
                    }

                    fixed!!.setResult("Die roll result: ${fixedResult.getDieRoll().get()}. ${this.name} air points aborted: ${fixedResult.getAbortedAirPoints()}air points shot down: ${fixedResult.getShotDownAirPoints()}", false)
                    this.fixedResult = fixedResult
                }

                if (rotaryInUse()) {
                    if (rotary.size != rotaryResults.size) {
                        throw Exception("The amount of used rotary is different than the number of rotary results")
                    }

                    var idx = 0
                    for (rotaryRow in rotary) {
                        val attrition = rotaryResults[idx].getAttritionToHelicopters()
                        val enableCheckBox = attrition > 0
                        rotaryRow.setResult("Die roll result: ${rotaryResults[idx].getDieRoll().get()}. ${this.name} rotary wing ${idx+1} suffered $attrition attrition.", enableCheckBox)
                        idx += 1

                    }

                    this.rotaryResult = rotaryResults
                }
            }

            private fun createRotaryRows() : List<AASettingRow> {
                val rotaryList = mutableListOf<AASettingRow>()
                val rotaryCount = combatSupport.getHelicopterCount()
                for (i in 0 until rotaryCount) {
                    val rowLayout = mainLayout.getChildAt(i+1) as LinearLayout
                    val settingRow = AASettingRow(rowLayout)
                    settingRow.removeCheckBox()
                    rotaryList.add(settingRow)
                }

                val maxRotaryRows = 3
                val rotaryRowsToBeRemoved = maxRotaryRows - rotaryList.size
                for (i in 0 until rotaryRowsToBeRemoved) {
                    val idx = maxRotaryRows - i
                    val layoutToRemove = mainLayout.getChildAt(idx) as LinearLayout
                    removeRow(layoutToRemove)
                }

                return rotaryList
            }

            private fun removeRow(linearLayout: LinearLayout) {
                while (linearLayout.childCount > 0) {
                    linearLayout.removeViewAt(0)
                }
            }

            private fun createFixedRow() : AASettingRow? {
                if (mainLayout.childCount != 4)    {
                    throw Exception("Child count of main layout is not 4")
                }

                val rowLayout = mainLayout.getChildAt(0) as LinearLayout

                return if (combatSupport.getAirPoints() != 0) {
                    return AASettingRow(rowLayout)
                } else {
                    removeRow(rowLayout)
                    null
                }
            }
        }

        val unitSelectionType = Communication.getUnitSelectionType(intent)

        val attackerAASettings = AASettings(findViewById<LinearLayout>(R.id.aa_against_defender), gameState.combatSupport!!.getDefenderCombatSupport()!!, "Defender")
        val defenderAASettings = AASettings(findViewById<LinearLayout>(R.id.aa_against_attacker), gameState.combatSupport!!.getAttackerCombatSupport()!!, "Attacker")

        fun addTextFieldListener(editText: EditText, onTextChanged: (String) -> Unit): Unit =
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(editText.text.toString() == "") {
                        editText.setText("0")
                    }

                    onTextChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })


        val aaFire = Tables.AAFire()

        var isFirstClick = true

        val aaApply = findViewById<Button>(R.id.apply)

        aaApply.setOnClickListener {

            if (isFirstClick) {

                fun executeAA(aaValue : Int?, againstBombardingRotary : Boolean): Tables.AAFire.Result? {
                    if (aaValue == null) {
                        return null
                    }

                    val dice = Dice()
                    val result = dice.roll()
                    return aaFire.getResult(result, aaValue, againstBombardingRotary)
                }

                fun executeAaAndSetResults(aaSettings: AASettings) {
                    val aaResultFixed = executeAA(aaSettings.getAaAgainstFixed(), false)
                    val aaResultRotary = mutableListOf<Tables.AAFire.Result>()
                    for (aaAgainstRotary in aaSettings.getAaAgainstRotary()) {
                        val bombardingRotary = unitSelectionType == UnitSelectionTypes.BOMBARDMENT
                        val resultAgainstRotary = executeAA(aaAgainstRotary, bombardingRotary)
                            ?: throw Exception("AA against rotary shouldn't be null")
                        aaResultRotary.add(resultAgainstRotary)
                    }

                    aaSettings.setResults(aaResultFixed, aaResultRotary)
                }

                executeAaAndSetResults(defenderAASettings)


                if (unitSelectionType != UnitSelectionTypes.BOMBARDMENT) {
                    if (attackerAASettings == null) {
                        throw Exception("attackerAASettings shouldn't be null when unitselectiontype is not bombardment")
                    }

                    executeAaAndSetResults(attackerAASettings)
                }
                aaApply.text = "Apply AA resuls"
                isFirstClick = false


            } else {

                fun adjustCombatSupport(combatSupport: CombatSupport) : CombatSupport {
                    if (defenderAASettings.fixedInUse()) {
                        combatSupport.adjustAirPoints(defenderAASettings.getFixedResult()!!)
                    }

                    if (defenderAASettings.rotaryInUse()) {
                        val destroyedRotaries = defenderAASettings.getDestroyedRotariesIndexes()
                        for (rotary in destroyedRotaries) {
                            combatSupport.adjustHelicopterPoints(rotary)
                        }
                    }

                    return combatSupport
                }

                var attackerCS = gameState.combatSupport!!.getAttackerCombatSupport()!!
                var defenderCS = gameState.combatSupport!!.getDefenderCombatSupport()!!

                attackerCS = adjustCombatSupport(attackerCS)
                defenderCS = adjustCombatSupport(defenderCS)

                gameState.combatSupport!!.setAttackerCombatSupport(attackerCS)
                gameState.combatSupport!!.setDefenderCombatSupport(defenderCS)

                if (unitSelectionType != UnitSelectionTypes.BOMBARDMENT) {
                    val intent = Intent(this, CombatResolutionActivity::class.java)
                    intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

                    startActivity(intent)
                    finish()
                } else {
                    // calculate bombardment result now
                    intent = Intent(this, MainActivity::class.java)

                    val bombardmentResultStr = Bombardment.resolveBombardment(gameState)

                    Helpers.showInfoDialog(
                        this,
                        bombardmentResultStr.first,
                        "Understood", null,
                        {
                            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                            startActivity(intent)
                            finish()
                        },
                    )
                }


            }



        }





    }


}