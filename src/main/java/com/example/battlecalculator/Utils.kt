package com.example.battlecalculator

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
        fun getIntFromTextField(editText: EditText): Int {
            val number = editText.text.toString().toIntOrNull()
            if (number == null) {
                editText.setText("0")
                return 0
            }

            return number
        }

        fun addTextFieldListener(editText: EditText, onTextChanged: (String) -> Unit): Unit =
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (editText.text.toString() == "") {
                        editText.setText("0")
                    }
                    onTextChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        fun strToBool(str: String): Boolean {
            return if (str == "true") {
                true
            } else if (str == "false") {
                false
            } else {
                throw Exception("Could convert string $str to Boolean")
            }
        }

        class IconArrayAdapter(
            context: Context,
            private val items: List<String>,
            private val icons: List<Int> // List of resource IDs for the icons
        ) : ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, items) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setCompoundDrawablesWithIntrinsicBounds(icons[position], 0, 0, 0)
                return view
            }
        }

        fun showRadioButtonDialog(
            context: Context?,
            items: List<String>,
            icons: List<Int>,
            title: String? = null,
            buttonText: String,
            negativeButtonText: String? = null,
            selectedCallback: (String) -> Unit,
            negativeCallback: (() -> Unit)? = null
        ) {

            val builder = AlertDialog.Builder(context)
            val checkedItem = 0 // this will check the item at position 0
            val adapter = IconArrayAdapter(context!!, items, icons)

            title?.let { builder.setTitle(it) }

            builder.setSingleChoiceItems(adapter, checkedItem) { dialog, which ->
                selectedCallback(items[which])
            }

            builder.setPositiveButton(buttonText) { dialog, _ ->
                dialog.dismiss()
            }

            if (negativeCallback != null) {
                builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                    dialog.dismiss()
                    negativeCallback()
                }
            }

            val dialog = builder.create()
            dialog.show()
        }


        fun showInfoDialog(
            context: Context?,
            message: String, buttonText: String, negativeButtonText: String? = null,
            primaryCallback: () -> Unit, negativeCallback: (() -> Unit)? = null
        ) {

            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setPositiveButton(buttonText) { dialog, _ ->
                // Close the popup
                dialog.dismiss()
                primaryCallback()
            }
            if (negativeCallback != null) {
                builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                    // Close the popup
                    dialog.dismiss()
                    negativeCallback()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }

        fun getTurnDifference(
            earlierDayEnum: DayEnum, earlierHourEnum: HourEnum,
            laterDayEnum: DayEnum, laterHourEnum: HourEnum
        ): Int {
            val earlierTurn = earlierDayEnum.ordinal * HourEnum.values().size + earlierHourEnum.ordinal
            val laterTurn = laterDayEnum.ordinal * HourEnum.values().size + laterHourEnum.ordinal

            return when {
                earlierTurn == laterTurn -> 0
                laterTurn > earlierTurn -> laterTurn - earlierTurn
                else -> (DayEnum.values().size * HourEnum.values().size) - earlierTurn + laterTurn
            }
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

        fun setImageViewForUnit(imageView: ImageView, unitState: UnitState, isPosture : Boolean, activity: AppCompatActivity, context: Context) {
            val imageName = if (isPosture) {
                getPostureFileName(unitState)
            } else {
                getImageFileName(unitState.unit!!.name)
            }
            val currentUnitDrawable =
                getDrawable(imageName, activity, context, context.applicationInfo)
                    ?: throw Exception("Unable to get drawable for $imageName")

            imageView.setImageDrawable(currentUnitDrawable)
        }

        private fun getPostureFileName(unitState: UnitState): String {
            if (unitState.posture == null) {
                throw Exception("Tried to convert empty posture to image file name")
            }

            return when (unitState.posture) {
                PostureEnum.SCRN -> "screen_smaller"
                PostureEnum.DEF -> "defense_smaller"
                PostureEnum.RDEF -> "rigid_defense_smaller"
                PostureEnum.ASL -> "assault_smaller"
                PostureEnum.FASL -> "full_assault_smaller"
                PostureEnum.TAC -> "tactical_smaller"
                PostureEnum.REFT -> "refit_smaller"
                PostureEnum.MASL -> "march_assault_smaller"
                PostureEnum.ROAD -> "road_smaller"
                PostureEnum.REC -> "recon_smaller"
                PostureEnum.MOV -> "road_smaller"
                PostureEnum.ADEF -> "area_defense_smaller"
                else -> throw Exception("Unrecognized posture: ${unitState.posture}")
            }

        }

        fun getDrawable(ImageName: String, activity: AppCompatActivity, context: Context, applicationInfo: ApplicationInfo): Drawable? {
            var identifier = activity.resources.getIdentifier(ImageName, "drawable", applicationInfo.packageName)
            if (identifier == 0) {
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