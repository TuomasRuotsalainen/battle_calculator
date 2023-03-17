package com.example.battlecalculator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import kotlin.Unit

class CombatSupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_artillery)

        var gameState = getGameState(intent)

        val unitSelectionType = Communication.getUnitSelectionType(intent)

        val totalSupport = findViewById<TextView>(R.id.total_support)

        val header = findViewById<TextView>(R.id.header)
        header.text = "Assign $unitSelectionType combat support"

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


        val combatSupportApply = findViewById<Button>(R.id.apply)

        val artilleryStrength = findViewById<EditText>(R.id.artillery_strength)
        val heliStrength1 = findViewById<EditText>(R.id.helicopter_barrage_strength)
        val heliStrength2 = findViewById<EditText>(R.id.helicopter_barrage_strength_2)
        val heliStrength3 = findViewById<EditText>(R.id.helicopter_barrage_strength_3)
        val airPoints = findViewById<EditText>(R.id.air_points_strength)

        val textFields = listOf(artilleryStrength, heliStrength1, heliStrength2, heliStrength3, airPoints)

        val insideCAS = findViewById<CheckBox>(R.id.cas_enabled)

        fun createCombatSupport() : CombatSupport {
            return CombatSupport(
                Helpers.getIntFromTextField(artilleryStrength),
                Helpers.getIntFromTextField(airPoints),
                mutableListOf(Helpers.getIntFromTextField(heliStrength1), Helpers.getIntFromTextField(heliStrength2), Helpers.getIntFromTextField(heliStrength3)),
                insideCAS.isChecked,
                unitSelectionType == UnitSelectionTypes.ATTACKER,
                null, null
            )
        }

        fun updateCombatSupportValue() {
            val combatSupport = createCombatSupport()
            val combatSupportValue = combatSupport.getTotalSupport()
            totalSupport.text = "Total combat support value: $combatSupportValue / 16"
        }

        for (textField in textFields) {
            textField.setText("0")
            addTextFieldListener(textField) {
                updateCombatSupportValue()
            }
        }

        insideCAS.setOnClickListener {
            updateCombatSupportValue()
        }

        val ewRulesBtn = findViewById<Button>(R.id.ew_rules)

        ewRulesBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("""EW points usage conditions:
                |1. The HQ must be within 4 hexes of the enemy unit
                |2. At least one involved unit must be under the HQ
                |3. The hex under attack must be within the HQ command range""".trimMargin())
            builder.setPositiveButton("Understood") { dialog, _ ->
                // Close the popup
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        combatSupportApply.setOnClickListener{
            val intent = Intent(this, EWInputActivity::class.java)
            intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), unitSelectionType.toString())

            if (gameState.combatSupport == null) {
                gameState.combatSupport = CombatSupportSelection()
            }

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                gameState.combatSupport!!.setAttackerCombatSupport(createCombatSupport())
            } else {
                gameState.combatSupport!!.setDefenderCombatSupport(createCombatSupport())
            }

            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }
    }



}