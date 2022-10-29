package com.example.battlecalculator

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import java.util.logging.Logger

class PostureAndAttackTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posture_and_attack_type)

        val unitSelectionType = Communication.getUnitSelectionType(intent)
        val gameState = getGameState(intent)
        if (gameState.attackingUnit == null) {
            throw Exception("Attacking unit state is null")

        }

        if (gameState.attackingUnit!!.unit == null) {
            throw Exception("Attacking unit is null")
        }


        val currentUnitState : UnitState
        currentUnitState = if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
            gameState.attackingUnit!!
        } else {
            gameState.getDefendingUnitStateWithoutPosture()
                ?: // This means that we should not have started this activity any more
                throw Exception("Started a posture and attack type activity for defender when all defending units have a posture already")
        }

        val currentUnitView = findViewById<TextView>(R.id.infotext)

        val postures = Postures()

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
            }

            prepared.setOnClickListener {
                hasty.isChecked = false
                gameState.attackType = AttackTypeEnum.PREPARED
            }

        } else {

            val attackTypeGroup = findViewById<RadioGroup>(R.id.attackTypeRadio)
            attackTypeGroup.removeAllViews()
        }

        val applyButton = findViewById<Button>(R.id.groudcombatApply)

        val nextIntent = Intent(this, DisengagementActivity::class.java)
        applyButton.setOnClickListener{

            if (unitSelectionType == UnitSelectionTypes.ATTACKER) {
                val attackingUnit = gameState.attackingUnit
                attackingUnit!!.posture = selectedPosture
                gameState.attackingUnit = attackingUnit
            } else {
                throw Exception("Not implemented")
            }

            nextIntent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(nextIntent)
            finish()
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




















