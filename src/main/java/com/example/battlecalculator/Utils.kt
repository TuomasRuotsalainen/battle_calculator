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

class Images {
    companion object Tools {
        fun getImageFileName(unitName : String): String {
            if (unitName == "") {
                throw Exception("Tried to convert empty unitName to image file name")
            }

            return "unit_" + unitName + "_smaller"
        }
    }
}