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

class Combat1Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat_1)


        var gameState = getGameState(intent)

        gameState = getMockGameState()


        val groundAttackButton = findViewById<Button>(R.id.combat1_apply)

        groundAttackButton.setOnClickListener{
            intent.putExtra(IntentExtraIDs.GAMESTATE.toString(), gameState.getStateString())

            startActivity(intent)
            finish()
        }



        }
}