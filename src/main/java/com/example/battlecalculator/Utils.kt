package com.example.battlecalculator

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.CountDownLatch
import kotlin.Unit

class Utils {
    companion object IntentTools {
        fun getStringFromIntent(intent: Intent, id : String) : String {
            return getStringFromIntentIfExists(intent, id)
                ?: throw Exception("Gamestate intent string is null for id $id")
        }

        fun getStringFromIntentIfExists(intent: Intent, id : String) : String? {
            return intent.getStringExtra(id)
        }
    }

}

class Helpers {
    companion object General {
        fun getIntFromTextField(editText: EditText) : Int {
            return editText.text.toString().toIntOrNull() ?: throw Exception("Encountered null text field with value ${editText.text}")
        }

        fun addTextFieldListener(editText: EditText, onTextChanged: (String) -> Unit): Unit =
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(editText.text.toString() == "") {
                        editText.setText("0")
                    }
                    onTextChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        fun strToBool(str : String) : Boolean {
            return if (str == "true") {
                true
            } else if (str == "false") {
                false
            } else {
                throw Exception("Could convert string $str to Boolean")
            }
        }

        fun showInfoDialog(context : Context?, message : String, callback: () -> Unit) {

            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setPositiveButton("Understood") { dialog, _ ->
                // Close the popup
                dialog.dismiss()
                callback()
            }

            val dialog = builder.create()
            dialog.show()
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