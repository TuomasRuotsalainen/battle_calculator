package com.example.battlecalculator

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.battlecalculator.Helpers.General.addTextFieldListener
import com.example.battlecalculator.Helpers.General.getIntFromTextField
import kotlin.Unit

class EWInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electronic_warfare_assignment)

        val gameState = getGameState(intent)
        val unitSelectionType = Communication.getUnitSelectionType(intent)

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }

        val currentPlayerIsNATO = if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            gameState.activeAlliance == Alliances.NATO
        } else {
            gameState.activeAlliance != Alliances.NATO
        }

        val ewPointInput = findViewById<EditText>(R.id.ew_points_input)
        val hqAttritionInput = findViewById<EditText>(R.id.hq_attrition_input)

        ewPointInput.setText("0")
        hqAttritionInput.setText("0")

        val ewEstimationText = findViewById<TextView>(R.id.ew_estimation)

        val pactUsesEWAgainstNato = -1
        val natoUsesEwAgainstPact = 1
        val enemyUnitInCityModifier = -1
        val enemyUnitInTownModifier = -2
        val hqAttritionModifier = -1

        val attackerInCityCheckBox = findViewById<CheckBox>(R.id.attacker_in_city)
        val attackerInTownCheckBox = findViewById<CheckBox>(R.id.attacker_in_town)

        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            val parentView = attackerInCityCheckBox.parent as ViewGroup
            parentView.removeView(attackerInCityCheckBox)
            parentView.removeView(attackerInTownCheckBox)

            // Make sure constraints are followed after checkboxes are removed
            val hqAtr = findViewById<LinearLayout>(R.id.hq_attrition)
            val params = hqAtr.layoutParams as ConstraintLayout.LayoutParams
            params.bottomToTop = R.id.ew_estimation
            hqAtr.layoutParams = params

            val ewEstimationTextParams = ewEstimationText.layoutParams as ConstraintLayout.LayoutParams
            ewEstimationTextParams.topToBottom = R.id.hq_attrition
            ewEstimationText.layoutParams = ewEstimationTextParams


        }

        var enemyUnitInCity = false
        var enemyUnitInTown = false

        var currentModifier = 0

        val ewTable = Tables.EW()

        fun calculateCurrentModifier() {
            var infoText = ""

            var modifier = 0
            modifier += if (currentPlayerIsNATO) {
                infoText += "NATO EW vs PACT: $natoUsesEwAgainstPact "
                natoUsesEwAgainstPact
            } else {
                infoText += "PACT EW vs NATO: $pactUsesEWAgainstNato "
                pactUsesEWAgainstNato
            }

            if (enemyUnitInTown && enemyUnitInCity) {
                throw Exception("In this context it shouldn't be possible to be in city and town at the same time")
            }

            if (enemyUnitInCity) {
                infoText += "Enemy unit in city: $enemyUnitInCityModifier "
                modifier += enemyUnitInCityModifier
            }

            if (enemyUnitInTown) {
                infoText += "Enemy unit in town: $enemyUnitInTownModifier "
                modifier += enemyUnitInTownModifier
            }

            val hqAttrition = getIntFromTextField(hqAttritionInput)
            var ewPointsInUse = getIntFromTextField(ewPointInput)
            if (ewPointsInUse > 6) {
                ewPointInput.setText("6")
                ewPointsInUse = 6
            }

            if (hqAttrition > 0) {
                modifier += hqAttrition*hqAttritionModifier
                infoText += "HQ attrition: ${hqAttrition*hqAttritionModifier}"
            }

            val meanResult = ewTable.getResultForModifiedRoll(5 + currentModifier, ewPointsInUse)

            val meanCombatModifier = if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                meanResult.combatModifier.first
            } else {
                meanResult.combatModifier.second
            }

            infoText += "\n\nMean EW result: Combat modifier ${meanCombatModifier}."
            for (effect in meanResult.effects) {
                if (effect == EwEffectEnum.ENEMY_ARTILLERY_HALVED) {
                    infoText += "Enemy artillery combat support halved"
                }

                if (effect == EwEffectEnum.ENEMY_AVIATION_HALVED) {
                    infoText += "Enemy aviation combat support halved"
                }

                if (effect == EwEffectEnum.ENEMY_COMMAND_DISRUPTED) {
                    infoText += "Enemy out of command and unable to use front line command"
                }
            }

            if (meanResult.effects.isEmpty()) {
                infoText += "No additional effects."
            }

            val totalModifierText = "Die roll modifier: $modifier\n\n"
            val combinedText = totalModifierText + infoText

            ewEstimationText.text = combinedText

            currentModifier = modifier

        }

        attackerInCityCheckBox.setOnClickListener {
            if (attackerInCityCheckBox.isChecked) {
                attackerInTownCheckBox.isChecked = false
            }

            enemyUnitInTown = attackerInTownCheckBox.isChecked
            enemyUnitInCity = attackerInCityCheckBox.isChecked

            calculateCurrentModifier()
        }

        attackerInTownCheckBox.setOnClickListener {
            if (attackerInTownCheckBox.isChecked) {
                attackerInCityCheckBox.isChecked = false
            }

            enemyUnitInTown = attackerInTownCheckBox.isChecked
            enemyUnitInCity = attackerInCityCheckBox.isChecked

            calculateCurrentModifier()
        }


        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            if (gameState.hexTerrain!!.features[TerrainEnum.TOWN] == true) {
                enemyUnitInTown = true
            }

            if (gameState.hexTerrain!!.features[TerrainEnum.CITY] == true) {
                enemyUnitInTown = false
                enemyUnitInCity = true
            }
        }

        addTextFieldListener(ewPointInput) {
            calculateCurrentModifier()
        }

        addTextFieldListener(hqAttritionInput) {
            calculateCurrentModifier()
        }

        calculateCurrentModifier()


        val applyButton = findViewById<Button>(R.id.apply)

        applyButton.setOnClickListener {

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {

                val combatSupport = gameState.combatSupport!!.getAttackerCombatSupport()
                combatSupport!!.setEW(getIntFromTextField(ewPointInput), currentModifier)
                gameState.combatSupport!!.setAttackerCombatSupport(combatSupport)

                val intent = Intent(this, CombatSupportActivity::class.java)

                intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())
                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                startActivity(intent)
                finish()

            } else {
                val combatSupport = gameState.combatSupport!!.getDefenderCombatSupport()
                combatSupport!!.setEW(getIntFromTextField(ewPointInput), currentModifier)
                gameState.combatSupport!!.setDefenderCombatSupport(combatSupport)

                val intent = if (gameState.combatSupport!!.isAirBeingUsed()) {
                    Intent(this, AAFireActivity::class.java)
                } else {
                    Intent(this, CombatResolutionActivity::class.java)
                }

                intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                startActivity(intent)
                finish()
            }

        }

    }

}