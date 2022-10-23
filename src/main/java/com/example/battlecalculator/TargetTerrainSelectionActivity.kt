package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.logging.Logger

class TargetTerrainSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_terrain_selection)

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

        val applyButton = findViewById<Button>(R.id.groudcombatApply)

        val intent = Intent(this, DisengagementActivity::class.java)
        applyButton.setOnClickListener{
            //Toast.makeText(this, "Unit type: $radioButtonStr, attack strength: ${strengths.first}, defense strength: ${strengths.second}", Toast.LENGTH_LONG*10).show()
            startActivity(intent)
            finish()
        }


         */


    }


}