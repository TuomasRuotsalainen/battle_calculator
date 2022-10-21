package com.example.battlecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.Button
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TUOMAS TAG", "hello logging world")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val groundAttackButton = findViewById<Button>(R.id.groundAttackButton)
        val artillerBombardmentButton = findViewById<Button>(R.id.artillerBomabrdmentButton)

        val intent = Intent(this, GroundCombatActivity::class.java)
        groundAttackButton.setOnClickListener{
            startActivity(intent)
            finish()
        }



        }
}