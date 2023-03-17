package com.example.battlecalculator

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class PostureAndAttackTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posture_and_attack_type)

        val unitSelectionType = Communication.getUnitSelectionType(intent)
        val gameState = getGameState(intent)
        if (gameState.attackingUnit == null) {
            throw Exception("Attacking unit state is null")

        }

        val currentUnitState : UnitState = if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            gameState.attackingUnit!!
        } else {
            gameState.getDefendingUnitStateWithoutPosture() ?:
            // This means that we should not have started this activity any more
            throw Exception("Started a posture and attack type activity for defender when all defending units have a posture already")
        }

        // TODO command and engagement selections not working
        currentUnitState.attrition = 0 // TODO add proper setting for attrition

        fun setCommandStateButtons() {
            val normal = findViewById<RadioButton>(R.id.radio_command_status_none)
            val frontLine = findViewById<RadioButton>(R.id.radio_command_status_front_line)
            val outOfCommand = findViewById<RadioButton>(R.id.radio_command_status_out)

            normal.isChecked = true
            currentUnitState.commandState = CommandStateEnum.NORMAL

            normal.setOnClickListener {
                if (normal.isChecked) {
                    frontLine.isChecked = false
                    outOfCommand.isChecked = false
                    currentUnitState.commandState = CommandStateEnum.NORMAL

                }
            }

            frontLine.setOnClickListener {
                if (frontLine.isChecked) {
                    normal.isChecked = false
                    outOfCommand.isChecked = false
                    currentUnitState.commandState = CommandStateEnum.FRONT_LINE_COMMAND
                }
            }

            outOfCommand.setOnClickListener {
                if (outOfCommand.isChecked) {
                    normal.isChecked = false
                    frontLine.isChecked = false
                    currentUnitState.commandState = CommandStateEnum.OUT_OF_COMMAND
                }
            }

        }

        fun setEngagementStateButtons() {
            val none = findViewById<RadioButton>(R.id.radio_engagement_status_none)
            val halfEngaged = findViewById<RadioButton>(R.id.radio_engagement_status_half_engaged)
            val engaged = findViewById<RadioButton>(R.id.radio_engagement_status_enganged)

            none.isChecked = true
            currentUnitState.engagementState = EngagementStateEnum.NONE

            none.setOnClickListener {
                if (none.isChecked) {
                    halfEngaged.isChecked = false
                    engaged.isChecked = false
                    currentUnitState.engagementState = EngagementStateEnum.NONE

                }
            }

            halfEngaged.setOnClickListener {
                if (halfEngaged.isChecked) {
                    none.isChecked = false
                    engaged.isChecked = false
                    currentUnitState.engagementState = EngagementStateEnum.HALF_ENGAGED

                }
            }

            engaged.setOnClickListener {
                if (engaged.isChecked) {
                    none.isChecked = false
                    halfEngaged.isChecked = false
                    currentUnitState.engagementState = EngagementStateEnum.ENGAGED

                }
            }

        }

        val headerText = findViewById<TextView>(R.id.postureSelHeader)
        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            headerText.text = "Attacking unit:"
        } else {
            headerText.text = "Defending unit:"
        }

        if (gameState.attackingUnit!!.unit == null) {
            throw Exception("Attacking unit is null")
        }


        val imageName = Images.getImageFileName(currentUnitState.unit!!.name)
        val currentUnitDrawable =
            Images.getDrawable(imageName, this, applicationContext, applicationInfo)
                ?: throw Exception("Unable to get drawable for $imageName")

        val currentUnitView = findViewById<ImageView>(R.id.currentUnitView)
        currentUnitView.setImageDrawable(currentUnitDrawable)

        val postures = Postures()
        val calculator = Calculator()

        val textView = findViewById<TextView>(R.id.combatDifferential)

        var selectedPosture : PostureEnum

        val postureAssault = findViewById<RadioButton>(R.id.radio_posture_assault)
        val postureMarchAssault = findViewById<RadioButton>(R.id.radio_posture_march_assault)
        val postureFullAssault = findViewById<RadioButton>(R.id.radio_posture_full_assault)
        val postureRecon = findViewById<RadioButton>(R.id.radio_posture_recon)
        val postureRefit = findViewById<RadioButton>(R.id.radio_posture_refit)
        val postureScreen = findViewById<RadioButton>(R.id.radio_posture_screen)
        val postureRigidDef = findViewById<RadioButton>(R.id.radio_posture_rigid_defence)
        val postureDefense = findViewById<RadioButton>(R.id.radio_posture_defence)
        val postureTactical = findViewById<RadioButton>(R.id.radio_posture_tactical)
        val postureAreaDef = findViewById<RadioButton>(R.id.radio_posture_area_defense)

        postureAssault.isChecked = true
        selectedPosture = PostureEnum.ASL

        val postureRadios = listOf(
            postureAssault, postureMarchAssault, postureFullAssault, postureRecon, postureRefit,
            postureScreen, postureRigidDef, postureDefense, postureTactical, postureAreaDef)

        for (postureRadio in postureRadios) {
            postureRadio.setOnClickListener {
                uncheckAllPostureRadios(postureRadios)
                postureRadio.isChecked = true
                selectedPosture = postures.getPostureEnumByStr(postureRadio.text.toString())
                val textContent = getTextViewString(currentUnitState.unit, gameState.attackType!!, selectedPosture, calculator, unitSelectionType, currentUnitState.commandState!!)
                textView.text = textContent

            }
        }

        if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            val hasty = findViewById<RadioButton>(R.id.radio_attaktype_hasty)
            val prepared = findViewById<RadioButton>(R.id.radio_attaktype_prepared)

            hasty.isChecked = true
            gameState.attackType = AttackTypeEnum.HASTY

            hasty.setOnClickListener {
                prepared.isChecked = false
                gameState.attackType = AttackTypeEnum.HASTY
                val textContent = getTextViewString(currentUnitState.unit, gameState.attackType!!, selectedPosture, calculator, unitSelectionType, currentUnitState.commandState!!)
                textView.text = textContent
            }

            prepared.setOnClickListener {
                hasty.isChecked = false
                gameState.attackType = AttackTypeEnum.PREPARED
                val textContent = getTextViewString(currentUnitState.unit, gameState.attackType!!, selectedPosture, calculator, unitSelectionType, currentUnitState.commandState!!)
                textView.text = textContent
            }

        } else {

            val attackTypeGroup = findViewById<RadioGroup>(R.id.attackTypeRadio)
            attackTypeGroup.removeAllViews()
        }

        setCommandStateButtons()
        setEngagementStateButtons()

        val textContent = getTextViewString(currentUnitState.unit, gameState.attackType!!, selectedPosture, calculator, unitSelectionType, currentUnitState.commandState!!)
        textView.text = textContent

        val applyButton = findViewById<Button>(R.id.groudcombatApply)

        applyButton.setOnClickListener{
            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                if (Postures().getPosture(selectedPosture).attack != null) {
                    val attackingUnit = gameState.attackingUnit
                    attackingUnit!!.posture = selectedPosture
                    gameState.attackingUnit = attackingUnit

                    val nextIntent = Intent(this, UnitSelectionActivity::class.java)
                    nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                    nextIntent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())

                    startActivity(nextIntent)
                    finish()
                }

            } else {

                currentUnitState.posture = selectedPosture
                gameState.setDefendingUnit(currentUnitState)

                val nextDefender = gameState.getDefendingUnitStateWithoutPosture()
                if (nextDefender != null) {
                    Log.d("TUOMAS TAG:", "Lets go to defender selection")
                    val nextIntent = Intent(this, PostureAndAttackTypeActivity::class.java)
                    nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                    nextIntent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.DEFENDER.toString())
                    Log.d("TUOMAS TAG:", "Starting activity with intent $nextIntent")
                    startActivity(nextIntent)
                    finish()
                } else {
                    val nextIntent = Intent(this, DisengagementActivity::class.java)
                    nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())
                    startActivity(nextIntent)
                    finish()
                }
            }


        }


    }

    private fun getTextViewString(unit: Unit, attack: AttackTypeEnum, postureEnum: PostureEnum, calculator: Calculator, unitSelectionType: UnitSelectionTypes, commandStateEnum: CommandStateEnum): String {
        // TODO implement this with calculator

        val posture = Postures().getPosture(postureEnum)

        return if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            if (posture.attack == null) {
                return "Unit with posture ${posture.enum} is not able to conduct attacks!"
            }

            val attackType = AttackType()

            val initialDifferential = calculator.getInitialAttackDifferential(unit = unit, posture = posture.enum, attackTypeEnum = attack)

            "Initial attack differential:\n${unit.attack} (unit) + ${posture.attack} (posture) + ${
                attackType.getCombatModifier(
                    attack
                )
            } (attack type)\n=$initialDifferential \n\nMP cost: ${attackType.getMPCost(posture.enum, attack)} (prepared assault)"
        } else {
            val initialDifferential = calculator.getInitialDefenseDifferential(unit, posture.enum)
            "Initial defense differential:\n${unit.defense} (unit) + ${posture.defense} (posture) \n=$initialDifferential"
        }

    }

    private fun uncheckAllPostureRadios(postureRadios: List<RadioButton>) {
        for (postureRadio in postureRadios) {
            postureRadio.isChecked = false
        }
    }

    private fun getImage(ImageName: String): Drawable? {
        val info = applicationInfo
        var identifier = this.resources.getIdentifier(ImageName, "drawable", applicationInfo.packageName)
        if (identifier == 0) {
            identifier = this.resources.getIdentifier("swamp_smaller", "drawable", applicationInfo.packageName)
        }

        return getDrawable(identifier)
    }
}




















