package com.example.battlecalculator

import android.content.Intent
import android.util.Log
import com.example.battlecalculator.BuildConfig.DEBUG


/*

stateString example
Z=0300;S=1,AU=12J-MASL;DU=13-DEF,14-ROAD,4-ROAD,AT=HASTY,HEX=PLAIN,FOREST,DEFENSE1,MINORRIVER;ACT=NATO
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/

fun GameState() : GameState {
    val stateStr = "Z=0300;S=1;AU=null;DU=null;AT=null;HEX=null;ACT=NATO"
    return GameState(stateStr)
}

fun getGameState(intent: Intent) : GameState {
    val gameStateString = Utils.getStringFromIntent(intent, IntentExtraIDs.GAMESTATE.toString())
    return GameState(gameStateString)
}

class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU, AT, HEX, ACT
    }

    private val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    var attackingUnit : UnitState? = getAttackUnitStates()
    var attackType : AttackTypeEnum? = getAttackTypeEnum()
    var defendingUnits : MutableList<UnitState> = getDefUnitsStates()
    var hexTerrain : HexTerrain? = getHexTerrainState()
    var activeAlliance : Alliances = getActiveFaction()

    fun getDefendingUnitStateWithoutPosture() : UnitState? {
        for (defendingUnitState in defendingUnits) {
            if (defendingUnitState.posture == null) {
                return defendingUnitState
            }
        }

        return null
    }

    private fun getActiveFaction(): Alliances {
        val activeFactionStr = dataMap[DataIDs.ACT.toString()] ?: throw Exception("Active faction ACT not defined")

        if (activeFactionStr == "null") {
            throw Exception("Active faction is null")
        }

        if (activeFactionStr == Alliances.NATO.toString()) {
            return Alliances.NATO
        } else if (activeFactionStr == Alliances.PACT.toString()) {
            return Alliances.PACT
        } else {
            throw Exception("Couldn't parse $activeFactionStr to faction")
        }
    }

    fun getStateString(): String {
        val attackingUnitStr = getAttackUnitStr()
        val defendingUnitsStr = getDefUnitsStr()
        val attackTypeStr = getAttackTypeStr()
        val hexTerrainStr = getHexTerrainStateStr()
        val allianceStr = getAllianceStr()

        return "Z=0300;S=1;AU=$attackingUnitStr;DU=$defendingUnitsStr;AT=$attackTypeStr;HEX=$hexTerrainStr;ACT=$allianceStr"
    }

    fun setDefendingUnit(unitState : UnitState) {
        if (unitState.unit == null) {
            throw Exception("Tried to set defending unit with null unit")
        }

        for (i in 0 until defendingUnits.size) {
            if (defendingUnits[i].unit == null) {
                continue
            }

            if (defendingUnits[i].unit!!.name == unitState.unit.name) {
                defendingUnits[i] = unitState
            }
        }
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

    private fun getHexTerrainState() : HexTerrain? {
        val hexTerrainStr = dataMap[DataIDs.HEX.toString()] ?: throw Exception("Hex terrain HEX to defined")

        if (hexTerrainStr == "null") {
            return null
        }

        val terrainFeatures = hexTerrainStr.split(",")
        val terrainFeatureEnums = mutableListOf<TerrainEnum>()
        for (terrainFeature in terrainFeatures) {
            for (terrainEnum in TerrainEnum.values()) {
                if (terrainFeature == terrainEnum.toString()) {
                    terrainFeatureEnums.add(terrainEnum)
                    break
                }
            }
        }

        return HexTerrain(terrainFeatureEnums)
    }

    private fun getHexTerrainStateStr() : String {
        if (hexTerrain == null) {
            return "null"
        }

        var str = ""
        var iterator = 0
        for (feature in hexTerrain!!.features) {
            str += feature.toString()

            iterator++
            if (iterator != hexTerrain!!.features.size) {
                str += ","
            }
        }

        return str
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

    private fun getAllianceStr() : String {
        return activeAlliance.toString()
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
