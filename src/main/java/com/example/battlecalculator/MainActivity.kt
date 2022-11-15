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

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TUOMAS TAG", "Getting oob")

        val state = GameState()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val natoSelection = findViewById<RadioButton>(R.id.faction_selection_nato)
        val pactSelection = findViewById<RadioButton>(R.id.faction_selection_pact)
        natoSelection.isChecked = true

        natoSelection.setOnClickListener {
            Log.d("TUOMAS", "NATO selected")
            state.activeAlliance = Alliances.NATO
        }

        pactSelection.setOnClickListener {
            Log.d("TUOMAS", "PACT selected")
            state.activeAlliance = Alliances.PACT
        }

        val groundAttackButton = findViewById<Button>(R.id.groundAttackButton)
        val artillerBombardmentButton = findViewById<Button>(R.id.artillerBomabrdmentButton)

        val intent = Intent(this, UnitSelectionActivity::class.java)
        intent.putExtra(IntentExtraIDs.UNITSELECTIONTYPE.toString(), UnitSelectionTypes.ATTACKER.toString())

        groundAttackButton.setOnClickListener{
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), state.getStateString())

            startActivity(intent)
            finish()
        }



        }
}