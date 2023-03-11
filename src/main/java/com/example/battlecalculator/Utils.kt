package com.example.battlecalculator

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat

class Utils {
    companion object IntentTools {
        fun getStringFromIntent(intent: Intent, id : String) : String {
            return intent.getStringExtra(id)
                ?: throw Exception("Gamestate intent string is null for id $id")
        }
    }

}

class Helpers {
    companion object General {
        fun getIntFromTextField(editText: EditText) : Int {
            return editText.text.toString().toIntOrNull() ?: throw Exception("Encountered null text field with value ${editText.text}")
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

        fun getDrawable(ImageName: String, activity: AppCompatActivity, context: Context, applicationInfo: ApplicationInfo): Drawable? {
            var identifier = activity.resources.getIdentifier(ImageName, "drawable", applicationInfo.packageName)
            if (identifier == 0) {
                Log.d("IMAGE DEBUG", "setting swamp")
                identifier = activity.resources.getIdentifier("swamp_smaller", "drawable", applicationInfo.packageName)
            }

            return ResourcesCompat.getDrawable(context.resources, identifier, null)
            //return getDrawable(identifier)
        }

    }
}

class Communication {
    companion object IntentExtras {
        fun getUnitSelectionType(intent: Intent) : UnitSelectionTypes {
            val unitSelectionTypeStr =
                Utils.getStringFromIntent(intent, IntentExtraIDs.UNITSELECTIONTYPE.toString())
            return if (unitSelectionTypeStr == UnitSelectionTypes.ATTACKER.toString()) {
                UnitSelectionTypes.ATTACKER
            } else if (unitSelectionTypeStr == UnitSelectionTypes.DEFENDER.toString()) {
                UnitSelectionTypes.DEFENDER
            } else {
                throw Exception("Unit selection type from intent is UNDEFINED")
            }
        }
    }
}