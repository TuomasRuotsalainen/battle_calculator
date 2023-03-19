package com.example.battlecalculator

import android.content.Intent
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.core.os.persistableBundleOf
import com.example.battlecalculator.BuildConfig.DEBUG
import com.example.battlecalculator.Helpers.General.strToBool


/*

stateString example
Z=0300;S=1,AU=12J-MASL;DU=13-DEF,14-ROAD,4-ROAD,AT=HASTY,HEX=PLAIN,FOREST,DEFENSE1,MINORRIVER;ACT=NATO;FIXED=ATTACKER_HALF_ENGAGED,PACT_DEFENDING_REAR
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/

fun GameState(conditions: Conditions) : GameState {
    val conditionString = conditions.toStateString()
    val stateStr = "CON=$conditionString;AU=null;DU=null;AT=null;HEX=null;ACT=NATO;FIXED=null;ADJ_AT=null;ADJ_DEF=null;COM_SUP=null"
    return GameState(stateStr)
}

fun getMockGameState(): GameState {
    val stateStr = "CON=H03-false-true-false-false;S=1;AU=74-ASL-false;DU=34-RDEF-false,74g-DEF-false;AT=HASTY;HEX=null;ACT=NATO;FIXED=ATTACKER_HALF_ENGAGED,PACT_DEFENDING_REAR;ADJ_AT=1;ADJ_DEF=1;COM_SUP=DEF-2-2,3,0-2-true&AT-0-0,0,0-5-false"
    return GameState(stateStr)
}

fun getGameState(intent: Intent) : GameState {
    val gameStateString = Utils.getStringFromIntent(intent, IntentExtraIDs.GAMESTATE.toString())
    return GameState(gameStateString)
}

fun getGameStateIfExists(intent : Intent) : GameState? {
    val gameStateString = Utils.getStringFromIntentIfExists(intent, IntentExtraIDs.GAMESTATE.toString())
        ?: return null

    return GameState(gameStateString)
}

class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU, AT, HEX, ACT, FIXED, ADJ_AT, ADJ_DEF, COM_SUP, CON
    }

    private val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    var conditions : Conditions = createConditions(dataMap[DataIDs.CON.toString()]!!)
    var attackingUnit : UnitState? = getAttackUnitStates()
    var attackType : AttackTypeEnum? = getAttackTypeEnum()
    var riverCrossingType : RiverCrossingTypeEnum? = getRiverCrossingTypeEnum()
    var defendingUnits : MutableList<UnitState> = getDefUnitsStates()
    var hexTerrain : HexTerrain? = getHexTerrainState()
    var activeAlliance : Alliances = getActiveFaction()
    var activeFixedModifiers : ActiveFixedModifiers = getFixedModifiers()
    var adjacentDefenderCount : Int? = getAdjacentDefenderUnits()
    var adjacentAttackerCount : Int? = getAdjacentAttackerUnits()
    var combatSupport : CombatSupportSelection? = getCombatSupportSelection()

    fun areSappersHit(result : Tables.GroundCombatResult) : Boolean {
        if (result.attackerSapperEliminated) {
            if (activeAlliance == Alliances.PACT) {
                return true
            }
        } else if (result.defenderSapperEliminated) {
            if (activeAlliance == Alliances.NATO) {
                return true
            }
        }

        return false
    }

    fun getDisengagingDefender() : UnitState? {
        for (defendingUnit in defendingUnits) {
            if (defendingUnit.disengagementOrdered) {
                return defendingUnit
            }
        }

        return null
    }

    fun updateConditions(newConditions: Conditions) {
        conditions = newConditions
    }

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
        val conditionsStr = conditions.toStateString()
        val attackingUnitStr = getAttackUnitStr()
        val defendingUnitsStr = getDefUnitsStr()
        val attackTypeStr = getAttackTypeStr()
        val hexTerrainStr = getHexTerrainStateStr()
        val allianceStr = getAllianceStr()
        val activeFixedModifiersStr = getActiveFixedModifiersStr()
        val adjacentDefenderCountStr = getAdjacentDefenderCountStr()
        val adjacentAttackerCountStr = getAdjacentAttackerCountStr()
        val combatSupportSelectionStr = getCombatSupportSelectionStr()

        return "CON=$conditionsStr;AU=$attackingUnitStr;DU=$defendingUnitsStr;AT=$attackTypeStr;HEX=$hexTerrainStr;ACT=$allianceStr;FIXED=$activeFixedModifiersStr;ADJ_DEF=$adjacentDefenderCountStr;ADJ_AT=$adjacentAttackerCountStr;COM_SUP=$combatSupportSelectionStr"
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

        if (defUnitStr == "") {
            defUnitStr = "null"
        }

        return defUnitStr
    }

    private fun getAttackUnitStr() : String {
        val unitState = attackingUnit ?: return "null"
        return unitState.getStateString()
    }

    private fun getHexTerrainState() : HexTerrain? {
        val hexTerrainStr = dataMap[DataIDs.HEX.toString()] ?: throw Exception("Hex terrain HEX not defined")

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

    private fun getCombatSupportSelection() : CombatSupportSelection? {
        val combatSupportSelection = CombatSupportSelection()

        val combatSupportStr = dataMap[DataIDs.COM_SUP.toString()] ?: throw Exception("Combat support COM_SUP not defined")

        if (combatSupportStr == "null") {
            return null
        }

        val selections = combatSupportStr.split("&")

        if (selections.isEmpty() || selections.size > 2) {
            throw Exception("Combat support string $combatSupportStr has a weird number of selections")
        }

        for (selection in selections) {
            val combatSupport = parseCombatSupport(selection)
            if (combatSupport.isAttacker) {
                if (combatSupportSelection.getAttackerCombatSupport() != null) {
                    throw Exception("Encountered two attacker combat support settings for combatSupportStr $combatSupportStr")
                }
                combatSupportSelection.setAttackerCombatSupport(combatSupport)
            } else {
                if (combatSupportSelection.getDefenderCombatSupport() != null) {
                    throw Exception("Encountered two defender combat support settings for combatSupportStr $combatSupportStr")
                }
                combatSupportSelection.setDefenderCombatSupport(combatSupport)
            }
        }

        return combatSupportSelection

    }

    private fun getFixedModifiers() : ActiveFixedModifiers {
        val fixedModifiersStr = dataMap[DataIDs.FIXED.toString()] ?: throw Exception("Fixed modifiers FIXED not defined")

        if (fixedModifiersStr == "null") {
            return getEmptyActiveFixedModifiers()
        }


        val activeModifiers = fixedModifiersStr.split(",")
        val fixedModifierEnums = mutableListOf<FixedModifierEnum>()
        for (activeModifier in activeModifiers) {
            for (fixedModifierEnum in FixedModifierEnum.values()) {
                if (activeModifier == fixedModifierEnum.toString()) {
                    fixedModifierEnums.add(fixedModifierEnum)
                    break
                }
            }
        }

        return ActiveFixedModifiers(fixedModifierEnums)

    }

    private fun getAdjacentAttackerUnits() : Int? {
        val adjacentAttackerUnitsStr = dataMap[DataIDs.ADJ_AT.toString()] ?: throw Exception("Fixed modifiers ADJ_AT not defined")
        return adjacentAttackerUnitsStr.toIntOrNull()
    }

    private fun getAdjacentDefenderCountStr() : String {
        return if (adjacentDefenderCount == null) {
            "null"
        } else {
            adjacentAttackerCount.toString()
        }
    }

    private fun getAdjacentAttackerCountStr() : String {
        return if (adjacentAttackerCount == null) {
            "null"
        } else {
            adjacentAttackerCount.toString()
        }
    }

    private fun getCombatSupportSelectionStr() : String {
        if (combatSupport == null) {
            return "null"
        }

        return combatSupport!!.toStateString()
    }

    private fun getAdjacentDefenderUnits() : Int? {
        val adjacentDefenderUnitsStr = dataMap[DataIDs.ADJ_DEF.toString()] ?: throw Exception("Fixed modifiers ADJ_DEF not defined")
        return adjacentDefenderUnitsStr.toIntOrNull()
    }

    private fun getActiveFixedModifiersStr() : String {
        if (activeFixedModifiers.map.size == 0) {
            return "null"
        }

        var str = ""
        var iterator = 0
        for (fixedModifierEnum in FixedModifierEnum.values()) {
            if (activeFixedModifiers.map[fixedModifierEnum] == true) {
                str += fixedModifierEnum.toString()
            }

            iterator++
            if (iterator != activeFixedModifiers.map.size) {
                str += ","
            }
        }

        return str
    }

    private fun getHexTerrainStateStr() : String {
        if (hexTerrain == null) {
            return "null"
        }

        var str = ""
        var iterator = 0
        for (feature in hexTerrain!!.features) {
            str += feature.key.toString()

            iterator++
            if (iterator != hexTerrain!!.features.size) {
                str += ","
            }
        }

        return str
    }

    private fun getRiverCrossingTypeEnum() : RiverCrossingTypeEnum? {
        // TODO: This has to be added to the state, view and everywhere
        return RiverCrossingTypeEnum.NONE
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

        if (auUnitStr == "") {
            throw Exception("aaUnitStr is empty")
        }

        if (auUnitStr != "null") {
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
                throw Exception("Encountered dataCell with bad content length: $dataCell. Associated game state string: $stateString")
            }

            dataMap[dataCellContents[0]] = dataCellContents[1]

        }

        return dataMap

    }

    private fun strToUnitState(str : String) : UnitState {
        val properties = str.split("-")
        var unit : Unit? = null
        var posture : PostureEnum? = null
        var attrition : Int? = null
        var commandState : CommandStateEnum? = null
        var engagementState : EngagementStateEnum? = null
        var disengagementOrdered : Boolean = false
        var attritionFromCombat : Int = 0

        val nullVal = "null"

        if (properties.isNotEmpty()) {
            val unitID = properties[0]
            if (unitID != "") {
                unit = oob.unitIndex[unitID]
            }
        }

        if (properties.size != 7) {
            throw Exception("Properties size of unit string $str is not 7")
        }

        val postureStr = properties[1]
        if (postureStr != nullVal) {
            posture = getPostureFromString(postureStr)
        }

        val attritionStr = properties[2]
        if (attritionStr != nullVal) {
            attrition = attritionStr.toInt()
        }

        val commandStateStr = properties[3]
        if (commandStateStr != nullVal) {
            commandState = getCommandStateFromString(commandStateStr)
        }

        val engagementStateStr = properties[4]
        if (engagementStateStr != nullVal) {
            engagementState = getEngagementStateFromString(engagementStateStr)
        }

        val disengagementOrderedStr = properties[5]
        if (disengagementOrderedStr != nullVal) {
            disengagementOrdered = strToBool(disengagementOrderedStr)
        }

        val attritionFromCombatStr = properties[6]
        if (attritionFromCombatStr != nullVal) {
            attritionFromCombat = attritionFromCombatStr.toInt()
        }

        return UnitState(unit, posture, attrition, commandState, engagementState, disengagementOrdered, attritionFromCombat)

    }
}

class UnitState(unitInput : Unit?, postureInput: PostureEnum?, attrition: Int?, commandStateEnum: CommandStateEnum?, engagementStateEnum : EngagementStateEnum?, disengagementOrdered : Boolean, attritionFromCombat : Int) {
    val unit = unitInput
    var posture = postureInput
    var attrition = attrition
    var commandState = commandStateEnum
    var engagementState = engagementStateEnum
    var disengagementOrdered = disengagementOrdered
    var attritionFromCombat = attritionFromCombat

    fun isInfantryOutInTheOpen() : Boolean {
        if (unit?.eatsArmourInCity() == true || unit?.type == UnitTypeEnum.RECON) {
            if (posture != PostureEnum.DEF && posture != PostureEnum.RDEF && posture != PostureEnum.ADEF && posture != PostureEnum.SCRN) {
                return true
            }
        }

        return false
    }

    fun orderDisengagementAttempt() {
        disengagementOrdered = true
    }

    fun getStateString() : String {

        val nullVal = "-null"

        var unitStr = ""

        if (unit == null) {
            return ""
        }

        unitStr += unit.name

        unitStr += if (posture != null) {
            "-$posture"
        } else {
            nullVal
        }

        unitStr += if (attrition != null) {
            "-$attrition"
        } else {
            nullVal
        }

        unitStr += if (commandState != null) {
            "-$commandState"
        } else {
            nullVal
        }

        unitStr += if (engagementState != null) {
            "-$engagementState"
         }else {
            nullVal
        }

        unitStr += "-$disengagementOrdered"

        unitStr += "-$attritionFromCombat"

        return unitStr
    }

}

fun strToHour(str : String) : HourEnum {
    val hour = when (str) {
        "H00" -> HourEnum.H00
        "H03" -> HourEnum.H03
        "H06" -> HourEnum.H06
        "H09" -> HourEnum.H09
        "H12" -> HourEnum.H12
        "H15" -> HourEnum.H15
        "H18" -> HourEnum.H18
        "H21" -> HourEnum.H21
        else -> throw Exception("Unrecognized hour: $str")
    }

    return hour
}

fun createConditions(str : String) : Conditions {
    val components = str.split("-")
    if (components.size != 5) {
        throw Exception("Conditions size is not 5")
    }

    val hour = strToHour(components[0])

    val fog = strToBool(components[1])
    val precipitation = strToBool(components[2])
    val pactUsesChem = strToBool(components[3])
    val natoUsesChem = strToBool(components[4])

    return Conditions(pactUsesChem, natoUsesChem, hour, fog, precipitation)

}

class Conditions(
    val pactUsesChem : Boolean, val natoUsesChem : Boolean,
    val hourEnum: HourEnum, val fog : Boolean, val precipitation : Boolean) {

    fun toStateString() : String {
        return "${hourEnum}-$fog-$precipitation-$pactUsesChem-$natoUsesChem"
    }

    fun isValid(): Boolean {
        if (!fog) {
            return true
        }

        if (hourEnum == HourEnum.H06 || hourEnum == HourEnum.H09) {
            return true
        }

        return false
    }

    fun toReadableString() : String {
        return "HOUR: $hourEnum FOG: $fog Precipitation: $precipitation PACT uses chem: $pactUsesChem NATO uses chem: $natoUsesChem"
    }

    fun isNight() : Boolean {
        return (hourEnum == HourEnum.H00 || hourEnum == HourEnum.H03 || hourEnum == HourEnum.H21)
    }

    private fun isClear(fog : Boolean, precipitation : Boolean) : Boolean {
        return (!fog && !precipitation)
    }
}

