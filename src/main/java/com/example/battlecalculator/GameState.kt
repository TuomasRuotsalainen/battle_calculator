package com.example.battlecalculator

import android.util.Log
import com.example.battlecalculator.BuildConfig.DEBUG


/*

stateString example
Z=0300;S=1,AU=12J;DU=13,14,4
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/

fun GameState() : GameState {
    val stateStr = "Z=0300;S=1;AU=null;DU=null"
    return GameState(stateStr)
}

class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU
    }

    private val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    var attackingUnit : Unit? = if(dataMap[DataIDs.AU.toString()] != "null") oob.unitIndex[dataMap[DataIDs.AU.toString()]] else null
    var defendingUnits : MutableList<Unit> = getDefUnits()

    fun getStateString(): String {
        val attackingUnitStr = attackingUnit?.name ?: ""
        val defendingUnitsStr = getDefUnitsStr()

        return "Z=0300;S=1;AU=$attackingUnitStr;DU=$defendingUnitsStr"
    }


    private fun getDefUnitsStr() :String {
        var defUnitStr = ""
        for (i in defendingUnits.indices) {
            defUnitStr += defendingUnits[i].name
            if (i<defendingUnits.size) {
                defUnitStr += ","
            }
        }

        return defUnitStr
    }

    private fun getDefUnits(): MutableList<Unit> {
        val unitList = mutableListOf<Unit>()
        val duStr = dataMap[DataIDs.DU.toString()] ?: throw Exception("duStr is null")
        if (duStr == "null") {
            return unitList
        }

        Log.d("TUOMAS TAG duStr", duStr)

        val duUnitsIds = duStr.split(",")
        for (duUnitId in duUnitsIds) {
            if (duUnitId == "") {
                continue
            }
            val unit = oob.unitIndex[duUnitId] ?: throw Exception("Can't find unit $duUnitId from oob")
            unitList.add(unit)
        }

        return unitList
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
}
