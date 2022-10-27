package com.example.battlecalculator

import android.content.Intent

class Utils {
    companion object IntentTools {
        fun getStringFromIntent(intent: Intent, id : String) : String {
            return intent.getStringExtra(id)
                ?: throw Exception("Gamestate intent string is null for id $id")
        }
    }
}