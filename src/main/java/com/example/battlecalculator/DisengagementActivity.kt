package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.battlecalculator.Helpers.General.addTextFieldListener
import com.example.battlecalculator.Helpers.General.getIntFromTextField
import com.example.battlecalculator.Helpers.General.showInfoDialog
import com.example.battlecalculator.Images.Tools.setImageViewForUnit

enum class DisengagementModifiers {
    UNIT_ENGAGED, UNIT_HALF_ENGAGED, NO_BLOCKING_ENEMIES, ENEMIES_BEHIND_MINOR, ENEMIES_BEHIND_MAJOR, ENEMIES_ENGAGED,
    UNIT_IN_TOWN, UNIT_IN_CITY, UNIT_IN_DEF_WORKS, FOG, NIGHT,
    LOW_SUPPORT, HIGH_SUPPORT
}

class DisengagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disengagement)

        val resultOptions = HashMap<DisengagemenResult, Pair<Boolean, Int>>()
        resultOptions[DisengagemenResult.F1] = Pair(false, 1)
        resultOptions[DisengagemenResult.S1] = Pair(true, 1)
        resultOptions[DisengagemenResult.S0] = Pair(true, 0)



        fun toString(disengagemenResult: DisengagemenResult) : String {
            val result = resultOptions[disengagemenResult]
            var str = if (result!!.first) {
                "Disengagement successful."
            } else {
                "Disengagement unsuccessful."
            }

            str += if (result.second > 0) {
                " Attrition points to disengaging unit: ${result.second}"
            } else {
                " No attrition to disengaging unit."
            }

            return str

        }

        var gameState = getGameState(intent)

        val disengagingUnit = gameState.getDisengagingDefender()
        if (disengagingUnit == null) {
            Log.d("DEBUG", "state string: ${gameState.getStateString()}")
            throw Exception("Disengaging unit is null")
        }

        val currentUnitView = findViewById<ImageView>(R.id.currentUnitView)
        val currentPostureView = findViewById<ImageView>(R.id.posture)


        setImageViewForUnit(currentUnitView, disengagingUnit, false, this, applicationContext, applicationInfo)
        setImageViewForUnit(currentPostureView, disengagingUnit, true, this, applicationContext, applicationInfo)

        val adjacentEnemyCombatUnitsField = findViewById<EditText>(R.id.adjacent_enemies_input)
        val adjacentFriendlyCombatUnitsField = findViewById<EditText>(R.id.adjacent_friendlies_input)
        val assignedCombatSupportField = findViewById<EditText>(R.id.assigned_combat_support)

        adjacentEnemyCombatUnitsField.setText("0")
        adjacentFriendlyCombatUnitsField.setText("0")
        assignedCombatSupportField.setText("0")

        var adjacentEnemyCombatUnits = 0
        var adjacentFriendlyCombatUnits = 0
        var assignedCombatSupport = 0

        val enemyAdjacency = -1
        val friendlyAdjacency = 1

        val estimationTextField = findViewById<TextView>(R.id.total)
        var estimationResult : DisengagemenResult

        var totalCombatSupport = 0

        val tables = Tables.DisengagementTable()

        val modifiers = HashMap<DisengagementModifiers, Pair<Int, Boolean>>()
        modifiers[DisengagementModifiers.UNIT_ENGAGED] = Pair(-2, false)
        modifiers[DisengagementModifiers.UNIT_HALF_ENGAGED] = Pair(-1, false)
        modifiers[DisengagementModifiers.NO_BLOCKING_ENEMIES] = Pair(1, false)
        modifiers[DisengagementModifiers.ENEMIES_BEHIND_MINOR] = Pair(1, false)
        modifiers[DisengagementModifiers.ENEMIES_BEHIND_MAJOR] = Pair(2, false)
        modifiers[DisengagementModifiers.ENEMIES_ENGAGED] = Pair(1, false)

        modifiers[DisengagementModifiers.UNIT_IN_TOWN] = Pair(1, false)
        modifiers[DisengagementModifiers.UNIT_IN_CITY] = Pair(2, false)
        modifiers[DisengagementModifiers.UNIT_IN_DEF_WORKS] = Pair(1, false)
        modifiers[DisengagementModifiers.FOG] = Pair(1, false)
        modifiers[DisengagementModifiers.NIGHT] = Pair(2, false)
        modifiers[DisengagementModifiers.LOW_SUPPORT] = Pair(1, false)
        modifiers[DisengagementModifiers.HIGH_SUPPORT] = Pair(2, false)

        fun enableModifier(modifier : DisengagementModifiers) {
            val pair = modifiers[modifier]
            val newPair = Pair(pair!!.first, true)
            modifiers[modifier] = newPair
        }

        var explanationText = ""
        var totalModifier = 0

        fun updateCalculations() {
            var text = ""
            var cumulativeModifier = 0

            for (modifier in modifiers) {
                if (modifier.value.second) {
                    text += modifier.toString() + ": " + modifier.value.first + "\n"
                    cumulativeModifier += modifier.value.first
                }
            }

            if (adjacentEnemyCombatUnits > 0) {
                val modifier = adjacentEnemyCombatUnits*enemyAdjacency
                text += "Adjacent enemy combat units: $modifier\n"
                cumulativeModifier += modifier
            }

            if (adjacentFriendlyCombatUnits > 0) {
                val modifier = adjacentFriendlyCombatUnits*friendlyAdjacency
                text += "Adjacent friendly combat units: $modifier\n"
                cumulativeModifier += modifier
            }

            text += "\nTotal modifier: $cumulativeModifier"
            text += "\n\nBigger modifier improves disengagement odds"

            explanationText = text
            totalModifier = cumulativeModifier

            val meanDieRoll = DiceRollResult(5)

            val posture = disengagingUnit!!.posture
                ?: throw Exception("Units posture is null. Unit string: ${disengagingUnit.getStateString()}")

            val unitType = disengagingUnit.unit!!.type
            estimationResult = tables.getResult(posture, unitType, meanDieRoll, totalModifier)

            text += "\n\n${toString(estimationResult)}"
        }

        fun disableModifier(modifier : DisengagementModifiers) {
            val pair = modifiers[modifier]
            val newPair = Pair(pair!!.first, false)
            modifiers[modifier] = newPair
        }

        fun setConstantModifiers() {
            if (gameState.hexTerrain!!.features.contains(TerrainEnum.CITY)) {
                enableModifier(DisengagementModifiers.UNIT_IN_CITY)
            } else if (gameState.hexTerrain!!.features.contains(TerrainEnum.TOWN)) {
                enableModifier(DisengagementModifiers.UNIT_IN_TOWN)
            }

            if (gameState.hexTerrain!!.features.contains(TerrainEnum.DEFENSE1) || gameState.hexTerrain!!.features.contains(TerrainEnum.DEFENSE3)) {
                enableModifier(DisengagementModifiers.UNIT_IN_DEF_WORKS)
            }

            if (gameState.conditions.fog) {
                enableModifier(DisengagementModifiers.FOG)
            }

            if (gameState.conditions.isNight()) {
                enableModifier(DisengagementModifiers.NIGHT)
            }
        }

        fun toggleCheckBox(checkBox : CheckBox, modifier: DisengagementModifiers, checkBoxToUncheck: CheckBox?, modifierToUncheck: DisengagementModifiers?) {
            if (checkBox.isChecked) {
                enableModifier(modifier)
                if (checkBoxToUncheck != null) {
                    checkBoxToUncheck.isChecked = false
                    disableModifier(modifierToUncheck!!)
                }
            } else {
                disableModifier(modifier)
            }

            updateCalculations()
        }

        setConstantModifiers()
        updateCalculations()

        val unitEngaged = findViewById<CheckBox>(R.id.rado_unit_engaged)
        val unitHalfEngaged = findViewById<CheckBox>(R.id.rado_unit_half_engaged)

        val noBlockingEnemies = findViewById<CheckBox>(R.id.radio_no_bloking_enemies)
        val behindMinor = findViewById<CheckBox>(R.id.radio_behind_minor_river)
        val behindMajor = findViewById<CheckBox>(R.id.radio_behind_major_river)
        val enemyHalfEngaged = findViewById<CheckBox>(R.id.radio_enemy_engaged)

        unitEngaged.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.UNIT_ENGAGED, unitHalfEngaged, DisengagementModifiers.UNIT_HALF_ENGAGED)
        }

        unitHalfEngaged.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.UNIT_HALF_ENGAGED, unitEngaged, DisengagementModifiers.UNIT_ENGAGED)
        }

        noBlockingEnemies.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.NO_BLOCKING_ENEMIES, null, null)
        }

        behindMinor.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.ENEMIES_BEHIND_MINOR, behindMajor, DisengagementModifiers.ENEMIES_BEHIND_MAJOR)
        }

        behindMajor.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.ENEMIES_BEHIND_MAJOR, behindMinor, DisengagementModifiers.ENEMIES_BEHIND_MINOR)
        }

        enemyHalfEngaged.setOnClickListener {
            toggleCheckBox(it as CheckBox, DisengagementModifiers.ENEMIES_ENGAGED, null, null)
        }

        addTextFieldListener(adjacentEnemyCombatUnitsField) {
            adjacentEnemyCombatUnits = getIntFromTextField(adjacentEnemyCombatUnitsField)
            updateCalculations()
        }

        addTextFieldListener(adjacentFriendlyCombatUnitsField) {
            adjacentFriendlyCombatUnits = getIntFromTextField(adjacentFriendlyCombatUnitsField)
            updateCalculations()
        }

        addTextFieldListener(assignedCombatSupportField) {
            assignedCombatSupport = getIntFromTextField(assignedCombatSupportField)
            if (assignedCombatSupport < 1) {
                disableModifier(DisengagementModifiers.HIGH_SUPPORT)
                disableModifier(DisengagementModifiers.LOW_SUPPORT)
            } else if (assignedCombatSupport > 2) {
                disableModifier(DisengagementModifiers.LOW_SUPPORT)
                enableModifier(DisengagementModifiers.HIGH_SUPPORT)
            } else {
                enableModifier(DisengagementModifiers.LOW_SUPPORT)
                disableModifier(DisengagementModifiers.HIGH_SUPPORT)
            }

            updateCalculations()
        }

        val explainButton = findViewById<Button>(R.id.explain)

        explainButton.setOnClickListener() {
            showInfoDialog(this, explanationText, "Understood", null, {})
        }

        val movement = Movement()
        val applyBtn = findViewById<Button>(R.id.apply)

        fun startNextActivity() {
            gameState.setDisengagementDone(disengagingUnit)
            val intent = if (gameState.getDisengagingDefender() != null) {
                Intent(this, DisengagementActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
            startActivity(intent)
            finish()
        }

        // If any sentient being ever tries to read this, I'm truly sorry
        applyBtn.setOnClickListener {
            val dice = Dice()
            val diceResult = dice.roll()
            val result = tables.getResult(disengagingUnit!!.posture!!, disengagingUnit.unit!!.type, diceResult, totalModifier)
            showInfoDialog(this, toString(result), "Understood", null, {
                var totalAttrition = disengagingUnit.attritionFromCombat + resultOptions[result]!!.second

                if (!resultOptions[result]!!.first) {
                    // Unsuccessful disengagement, set total attrition to defender
                    showInfoDialog(this, "This unit can't retreat\n\n1.Mark the unit as Engaged\n2. Apply $totalAttrition attrition points (attrition from combat and disengagement attempt)", "Understood", null, {
                        startNextActivity()
                    })
                } else {
                    if (totalAttrition > 0) {
                        totalAttrition -= 1
                    }
                    showInfoDialog(this, "This unit can retreat\n\n1.Mark the unit as Half-Engaged (if not already Engaged)\n2. Apply $totalAttrition attrition points (attrition from combat and disengagement attempt - 1)", "Normal retreat", "Hasty crossing over a minor river", {
                        //showInfoDialog(this, "retreat activity starts!") {}
                        startNextActivity()
                    }, {
                        val unitType = disengagingUnit.unit.type
                        val movementType = getMovementType(unitType)
                        val posture = disengagingUnit.posture!!
                        val movementMode = Tables.TerrainCombatTable.MovementMode().get(posture)
                        val maxAttritionRange = movement.getAttritionRangeForHastyCrossing(movementType,movementMode)
                        val attritionRoll = dice.roll()

                        if (attritionRoll.get() > maxAttritionRange) {
                            showInfoDialog(this, "River crossing roll: ${attritionRoll.get()}. You can retreat the unit over the minor river without suffering additional attrition.", "Understood", null, {
                                startNextActivity()
                            })
                        } else {
                            // One more attrition
                            showInfoDialog(this, "River crossing roll: ${attritionRoll.get()}. You can retreat the unit over the minor river, but it suffers one additional attrition.", "Understood", null, {
                                startNextActivity()
                            })
                        }

                    })
                }
            })
        }
    }

}