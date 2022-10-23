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
        Log.d("TUOMAS TAG", "Getting oob")

        val oob = OrderOfBattle()
        val oobElement = oob.getOOB()
        Log.d("TUOMAS TAG",  "alliance name: " + oobElement[0].allianceName)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val groundAttackButton = findViewById<Button>(R.id.groundAttackButton)
        val artillerBombardmentButton = findViewById<Button>(R.id.artillerBomabrdmentButton)

        val intent = Intent(this, TargetTerrainSelectionActivity::class.java)
        groundAttackButton.setOnClickListener{
            startActivity(intent)
            finish()
        }



        }
}