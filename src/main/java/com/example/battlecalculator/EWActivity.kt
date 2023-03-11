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

        if (gameState.combatSupport == null) {
            throw Exception("Combat support is null")
        }




    }


}