package com.example.battlecalculator

import android.content.Intent
import android.util.Log
import com.example.battlecalculator.BuildConfig.DEBUG


/*

stateString example
Z=0300;S=1,AU=12J-MASL;DU=13,14,4,AT=HASTY
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/

fun GameState() : GameState {
    val stateStr = "Z=0300;S=1;AU=null;DU=null;AT=null"
    return GameState(stateStr)
}

fun getGameState(intent: Intent) : GameState {
    val gameStateString = Utils.getStringFromIntent(intent, IntentExtraIDs.GAMESTATE.toString())
    return GameState(gameStateString)
}

class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU, AT
    }

    private val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    var attackingUnit : UnitState? = getAttackUnitStates()
    var attackType : AttackTypeEnum? = getAttackTypeEnum()
    var defendingUnits : MutableList<UnitState> = getDefUnitsStates()

    fun getDefendingUnitStateWithoutPosture() : UnitState? {
        for (defendingUnitState in defendingUnits) {
            if (defendingUnitState.posture == null) {
                return defendingUnitState
            }
        }

        return null
    }

    fun getStateString(): String {
        val attackingUnitStr = getAttackUnitStr()
        val defendingUnitsStr = getDefUnitsStr()
        val attackTypeStr = getAttackTypeStr()

        return "Z=0300;S=1;AU=$attackingUnitStr;DU=$defendingUnitsStr;AT=$attackTypeStr"
    }


    private fun getDefUnitsStr() :String {
        var defUnitStr = ""
        for (i in defendingUnits.indices) {

            val unitState = defendingUnits[i]
            val unitStr = unitState.getStateString()

            defUnitStr += unitStr

            if (i<defendingUnits.size) {
                defUnitStr += ","
            }
        }

        return defUnitStr
    }

    private fun getAttackUnitStr() : String {
        val unitState = attackingUnit ?: return ""
        return unitState.getStateString()
    }

    private fun getAttackTypeEnum() : AttackTypeEnum? {
        val attackTypeStr = dataMap[DataIDs.AT.toString()]
            ?: throw Exception("Attack type AT is not defined")

        if (attackTypeStr == "null") {
            return null
        }

        for (attackType in AttackTypeEnum.values()) {
            if (attackType.toString() == attackTypeStr) {
                return attackType
            }
        }

        throw Exception("Attack type $attackTypeStr not recognized")

    }

    private fun getAttackTypeStr() : String {
        if (attackType == null) {
            return "null"
        }

        return attackType.toString()
    }

    private fun getDefUnitsStates(): MutableList<UnitState> {
        val unitStateList = mutableListOf<UnitState>()
        val duStr = dataMap[DataIDs.DU.toString()] ?: throw Exception("duStr is null")
        if (duStr == "null") {
            return unitStateList
        }

        val duUnitsIds = duStr.split(",")
        for (duUnitId in duUnitsIds) {
            if (duUnitId == "") {
                continue
            }
            val unitState = strToUnitState(duUnitId)
            unitStateList.add(unitState)
        }

        return unitStateList
    }

    private fun getAttackUnitStates(): UnitState? {
        val auUnitStr = dataMap[DataIDs.AU.toString()]
            ?: throw Exception("Attacking unit cell not defined")

        if(auUnitStr != "null") {
            return strToUnitState(auUnitStr)
        }

        return null
    }

    private fun getDataMap(stateString: String) : HashMap<String,String> {
        val dataCells = stateString.split(";").toTypedArray()
        val dataMap = HashMap<String, String>()

        for (dataCell in dataCells) {
            val dataCellContents = dataCell.split("=")
            if (dataCellContents.size != 2) {
                throw Exception("Encountered dataCell with bad content length: $dataCell")
            }

            dataMap[dataCellContents[0]] = dataCellContents[1]

        }

        return dataMap

    }

    private fun strToUnitState(str : String) : UnitState {
        val properties = str.split("-")
        var unit : Unit? = null
        var posture : PostureEnum? = null

        if (properties.isNotEmpty()) {
            val unitID = properties[0]
            if (unitID != "") {
                unit = oob.unitIndex[unitID]
            }
        }

        if (properties.size > 1) {
            val postureStr = properties[1]
            if (postureStr != "") {
                posture = getPostureFromString(postureStr)
            }
        }

        return UnitState(unit, posture)

    }
}



class UnitState(unitInput : Unit?, postureInput: PostureEnum?) {
    val unit = unitInput
    var posture = postureInput

    fun getStateString() : String {
        var unitStr = ""

        if (unit == null) {
            return ""
        }

        unitStr += unit.name

        if (posture != null) {
            unitStr += "-$posture"
        }

        return unitStr
    }

}
