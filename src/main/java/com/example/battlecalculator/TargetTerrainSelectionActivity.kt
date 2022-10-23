package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.logging.Logger

class TargetTerrainSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_terrain_selection)

        val forestCheck = findViewById<CheckBox>(R.id.checkBoxForest)
        val plainCheck = findViewById<CheckBox>(R.id.checkBoxPlain)
        val townCheck = findViewById<CheckBox>(R.id.checkBoxTown)
        val cityCheck = findViewById<CheckBox>(R.id.checkBoxCity)
        val swampCheck = findViewById<CheckBox>(R.id.checkBoxSwamp)
        val minorRiverCheck = findViewById<CheckBox>(R.id.checkBoxMinorRiver)
        val majorRiverCheck = findViewById<CheckBox>(R.id.checkBoxMajorRiver)

        val defenseWorks1Check = findViewById<CheckBox>(R.id.checkBoxDefenseWorks1)
        val defenseWorks3Check = findViewById<CheckBox>(R.id.checkBoxDefenseWorks3)
        val bridgeCheck = findViewById<CheckBox>(R.id.checkBoxBridgeExists)

        plainCheck.isChecked = true

        defenseWorks3Check.setOnClickListener {
            defenseWorks1Check.isChecked = false
        }

        defenseWorks1Check.setOnClickListener {
            defenseWorks3Check.isChecked = false
        }

        majorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }
            if (majorRiverCheck.isChecked) {
                minorRiverCheck.isChecked = false
            }

        }

        minorRiverCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }

            if (minorRiverCheck.isChecked) {
                majorRiverCheck.isChecked = false
            }
        }

        bridgeCheck.setOnClickListener {
            if (!majorRiverCheck.isChecked && !minorRiverCheck.isChecked) {
                bridgeCheck.isChecked = false
            }
        }

        val applyButton = findViewById<Button>(R.id.terrain_apply)

        val intent = Intent(this, GroundCombatActivity::class.java)
        applyButton.setOnClickListener{
            //Toast.makeText(this, "Unit type: $radioButtonStr, attack strength: ${strengths.first}, defense strength: ${strengths.second}", Toast.LENGTH_LONG*10).show()
            startActivity(intent)
            finish()
        }


        /*
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

        val postureRadios = listOf(
            postureAssault, postureMarchAssault, postureFullAssault, postureRecon, postureRefit,
            postureScreen, postureRigidDef, postureDefense, postureTactical, postureAreaDef)

        for (postureRadio in postureRadios) {
            postureRadio.setOnClickListener {
                uncheckAllPostureRadios(postureRadios)
                postureRadio.isChecked = true
            }
        }




         */


    }



}