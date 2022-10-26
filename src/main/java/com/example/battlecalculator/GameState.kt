package com.example.battlecalculator

import org.json.JSONObject
import com.beust.klaxon.*


/*

stateString example
Z=0300;S=1,AU=12J;DU=13,14,4
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/


class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU
    }

    val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    val attackingUnit : Unit? = if(dataMap[DataIDs.AU.toString()] != "null") oob.unitIndex[dataMap[DataIDs.AU.toString()]] else null
    val defendingUnits = getDefUnits()

    fun getStateString() : String {
        val attackingUnitStr = attackingUnit?.name ?: ""
        val defendingUnitsStr = getDefUnitsStr()
        val stateStr = "Z=0300;S=1;AU=$attackingUnitStr;DU=$defendingUnitsStr"

        return stateStr
    }

    private fun getDefUnitsStr() {
        var defUnitStr = ""
        for (i in 0..defendingUnits.size) {
            defUnitStr += defendingUnits[i].name
            if (i<defendingUnits.size) {
                defUnitStr += ","
            }
        }
    }

    private fun getDefUnits(): List<Unit> {
        val unitList = mutableListOf<Unit>()
        val duStr = dataMap[DataIDs.DU.toString()]
        if (duStr == "null") {
            return unitList
        }

        if (duStr == null) {
            throw Exception("duStr is null")
        }

        val duUnitsIds = duStr.split(",")
        for (duUnitId in duUnitsIds) {
            val unit = oob.unitIndex[duUnitId] ?: throw Exception("Can't find unit $duUnitId from oob")
            unitList.add(unit)
        }

        return unitList
    }

    private fun getDataMap(stateString: String) : HashMap<String,String> {
        val dataCells = stateString.split(";").toTypedArray()
        val dataMap = HashMap<String, String>()

        for (dataCell in dataCells) {
            val dataCellContents = dataCell.split("")
            if (dataCellContents.size != 2) {
                throw Exception("Encountered dataCell with bad content length: $dataCell")
            }

            dataMap[dataCellContents[0]] = dataCellContents[1]

        }

        return dataMap

    }
}
