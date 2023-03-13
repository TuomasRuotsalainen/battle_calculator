package com.example.battlecalculator

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import kotlin.Unit

class EWActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electronic_warfare)

        var gameState = getGameState(intent)

        //val attackerEWPoints = gameState.combatSupport!!.getAttackerCombatSupport()!!.getEWPoints()
        //val defenderEWPoints = gameState.combatSupport!!.getDefenderCombatSupport()!!.getEWPoints()

        /*
        if (attackerEWPoints == 0 && defenderEWPoints == 0) {
            throw Exception("EW activity shouldn't be opened if no EW points in use")
        }*/

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }

        val ewTable = Tables.EW()

        val applyButton = findViewById<Button>(R.id.apply)

        applyButton.setOnClickListener {

        }

    }


}