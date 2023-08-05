package com.example.battlecalculator

import android.content.Intent
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.core.os.persistableBundleOf
import com.example.battlecalculator.BuildConfig.DEBUG
import com.example.battlecalculator.Helpers.General.strToBool
import kotlin.math.abs


/*

stateString example
Z=0300;S=1,AU=12J-MASL;DU=13-DEF,14-ROAD,4-ROAD,HEX=PLAIN,FOREST,DEFENSE1,MINORRIVER;ACT=NATO;FIXED=ATTACKER_HALF_ENGAGED,PACT_DEFENDING_REAR
zulu time = 03:00, state = 1, attacking unit id = 12J, unit ids in target hex 13,14,4

*/

fun GameState(conditions: Conditions) : GameState {
    val conditionString = conditions.toStateString()
    val stateStr = "CON=$conditionString;AU=null;DU=null;HEX=null;ACT=NATO;FIXED=null;ADJ_AT=null;ADJ_DEF=null;COM_SUP=null;SUP_CNT=null;DET_LVL=null;DET_MOD=null"
    return GameState(stateStr)
}

fun getMockGameState(): GameState {
    val stateStr = "CON=H03-false-true-false-false;S=1;AU=74-ASL-false;DU=34-RDEF-false,74g-DEF-false;HEX=null;ACT=NATO;FIXED=ATTACKER_HALF_ENGAGED,PACT_DEFENDING_REAR;ADJ_AT=1;ADJ_DEF=1;COM_SUP=DEF-2-2,3,0-2-true&AT-0-0,0,0-5-false"
    return GameState(stateStr)
}

fun getGameState(intent: Intent) : GameState {
    val gameStateString = Utils.getStringFromIntent(intent, IntentExtraIDs.GAMESTATE.toString())
    Log.d("GAMESTATESTR", gameStateString)
    return GameState(gameStateString)
}

fun getGameStateIfExists(intent : Intent) : GameState? {
    val gameStateString = Utils.getStringFromIntentIfExists(intent, IntentExtraIDs.GAMESTATE.toString())
        ?: return null

    return GameState(gameStateString)
}

class GameState(stateString : String) {

    private enum class DataIDs {
        AU, DU, HEX, ACT, FIXED, ADJ_AT, ADJ_DEF, COM_SUP, CON, SUP_CNT, DET_LVL, DET_MOD
    }

    private val dataMap = getDataMap(stateString)
    val oob = OrderOfBattle()

    var conditions : Conditions = createConditions(dataMap[DataIDs.CON.toString()]!!)
    var attackingUnit : UnitState? = getAttackUnitStates()
    //var attackType : AttackTypeEnum? = getAttackTypeEnum()
    var defendingUnits : MutableList<UnitState> = getDefUnitsStates()
    var hexTerrain : HexTerrain? = getHexTerrainState()
    var activeAlliance : Alliances = getActiveFaction()
    var activeFixedModifiers : ActiveFixedModifiers = getFixedModifiers()
    var adjacentDefenderCount : Int? = getAdjacentDefenderUnits()
    var adjacentAttackerCount : Int? = getAdjacentAttackerUnits()
    var combatSupport : CombatSupportSelection? = getCombatSupportSelection()
    var supportUnitCount : Int? = getSupportUnits()
    var detectionLevel : DetectionLevel? = getDetectionLevelBomb()
    var detectionLevelModifiers : MutableList<DetectionModifiers>? = getDetectionLevelModifiersBomb()


    fun reset() {
        attackingUnit = null
        //attackType = null
        defendingUnits = mutableListOf()
        hexTerrain = null
        activeFixedModifiers = ActiveFixedModifiers(mutableListOf())
        adjacentAttackerCount = null
        adjacentDefenderCount = null
        combatSupport = null
        supportUnitCount = null
        detectionLevel = null
        detectionLevelModifiers = null

    }

    fun setCommandStateFixedModifiers() {
        activeFixedModifiers.remove(FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND)
        activeFixedModifiers.remove(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND)
        activeFixedModifiers.remove(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC)
        activeFixedModifiers.remove(FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND)
        activeFixedModifiers.remove(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND)
        activeFixedModifiers.remove(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC)

        if (attackingUnit!!.commandState == CommandStateEnum.OUT_OF_COMMAND) {
            if (attackingUnit!!.isScreenOrRec()) {
                activeFixedModifiers.add(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND)
            } else {
                activeFixedModifiers.add(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC)
            }
        } else if (attackingUnit!!.commandState == CommandStateEnum.FRONT_LINE_COMMAND) {
            activeFixedModifiers.add(FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND)
        }

        val fixedModifiers = FixedModifiers()

        val activeDefenderCommandStates = mutableListOf<FixedModifierEnum?>()

        for (defendingUnit in defendingUnits) {
            val modifier = fixedModifiers.getCommandStateModifier(defendingUnit)
            activeDefenderCommandStates.add(modifier.second)
        }

        var frontLineCommandCount = 0
        var worstOutOfCommandSituation : FixedModifierEnum? = null

        for (commandState in activeDefenderCommandStates) {
            when (commandState) {
                null -> {}
                FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND -> frontLineCommandCount += 1
                FixedModifierEnum.DEFENDER_OUT_OF_COMMAND -> {
                    worstOutOfCommandSituation = FixedModifierEnum.DEFENDER_OUT_OF_COMMAND
                }
                FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC -> {
                    if (worstOutOfCommandSituation == null) {
                        worstOutOfCommandSituation = FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC
                    }
                }
                else -> {}
            }
        }

        if (worstOutOfCommandSituation == null && frontLineCommandCount == defendingUnits.size) {
            activeFixedModifiers.add(FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND)
        } else if (worstOutOfCommandSituation != null) {
            activeFixedModifiers.add(worstOutOfCommandSituation)
        }
    }


    // Boolean indicates whether all units that are out of command are in screen or rec posture
    /*fun getCommandState(isAttacker : Boolean) : Pair<CommandStateEnum, Boolean> {
        if (isAttacker) {
            return Pair(attackingUnit!!.commandState!!, attackingUnit!!.isScreenOrRec())
        }

        var everyoneFrontLineCommand = true
        var isOutOfCommandUnitInNormalPosture = false
        var someoneOutOfCommand = false
        for (defendingUnit in defendingUnits) {
            if (defendingUnit.commandState == CommandStateEnum.OUT_OF_COMMAND) {
                if (!isOutOfCommandUnitInNormalPosture) {
                    isOutOfCommandUnitInNormalPosture = !defendingUnit.isScreenOrRec()
                }
                someoneOutOfCommand = true
            }

            if (defendingUnit.commandState != CommandStateEnum.FRONT_LINE_COMMAND) {
                everyoneFrontLineCommand = false
            }
        }

        if (someoneOutOfCommand) {
            return Pair(CommandStateEnum.OUT_OF_COMMAND, !isOutOfCommandUnitInNormalPosture)
        }

        if (everyoneFrontLineCommand) {
            return Pair(CommandStateEnum.FRONT_LINE_COMMAND, false)
        }

        return Pair(CommandStateEnum.NORMAL, false)
    }*/

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

    fun setDisengagementDone(unitState : UnitState) {
        val newList : MutableList<UnitState> = mutableListOf()
        for (defendingUnit in defendingUnits) {
            if (defendingUnit.unit!!.name != unitState.unit!!.name) {
                newList.add(defendingUnit)
            }
        }
        this.defendingUnits = newList
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
        //val attackTypeStr = getAttackTypeStr()
        val hexTerrainStr = getHexTerrainStateStr()
        val allianceStr = getAllianceStr()
        val activeFixedModifiersStr = getActiveFixedModifiersStr()
        val adjacentDefenderCountStr = getAdjacentDefenderCountStr()
        val adjacentAttackerCountStr = getAdjacentAttackerCountStr()
        val combatSupportSelectionStr = getCombatSupportSelectionStr()
        val supportUnitsStr = getSupportUnitsStr()
        val detectionLevelStr = getDetectionLevelStr()
        val detectionModifiersStr = getDetectionLevelModifiersStr()

        return "CON=$conditionsStr;AU=$attackingUnitStr;DU=$defendingUnitsStr;HEX=$hexTerrainStr;ACT=$allianceStr;FIXED=$activeFixedModifiersStr;ADJ_DEF=$adjacentDefenderCountStr;ADJ_AT=$adjacentAttackerCountStr;COM_SUP=$combatSupportSelectionStr;SUP_CNT=$supportUnitsStr;DET_LVL=$detectionLevelStr;DET_MOD=$detectionModifiersStr"
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
        Log.d("DEBUG ", "1")
        val unitState = attackingUnit ?: return "null"
        Log.d("DEBUG ", "2")
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

    private fun getSupportUnits() : Int? {
        val getSupportUnitCountStr = dataMap[DataIDs.SUP_CNT.toString()] ?: throw Exception("Fixed modifiers SUP_CNT not defined")
        return getSupportUnitCountStr.toIntOrNull()
    }

    private fun getDetectionLevelBomb() : DetectionLevel? {
        val detectionLevelStr = dataMap[DataIDs.DET_LVL.toString()] ?: throw Exception("DET_LVL not defined")
        if (detectionLevelStr == "null" || detectionLevelStr == "") {
            return null
        }

        for (enum in DetectionLevel.values()) {
            if (detectionLevelStr == enum.toString()) {
                return enum
            }
        }

        throw Exception("Couldn't map $detectionLevelStr to detection level")
    }

    private fun getDetectionLevelModifiersBomb() : MutableList<DetectionModifiers>? {
        val detectionLevelStr = dataMap[DataIDs.DET_MOD.toString()] ?: throw Exception("DET_MOD not defined")
        if (detectionLevelStr == "") {
            return null
        }

        val levelStrs = detectionLevelStr.split(",")
        val modifiers = mutableListOf<DetectionModifiers>()
        for (levelStr in levelStrs) {
            for (enum in DetectionModifiers.values()) {
                if (levelStr == enum.toString()) {
                    modifiers.add(enum)
                    break
                }
            }
        }

        return modifiers
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

    private fun getSupportUnitsStr() : String {
        if (supportUnitCount == null) {
            return "null"
        }

        return supportUnitCount.toString()
    }

    private fun getDetectionLevelModifiersStr() : String {
        if (detectionLevelModifiers == null) {
            return "null"
        }

        var str = ""

        if (detectionLevelModifiers!!.isEmpty()) {
            return "null"
        }

        for (modifier in detectionLevelModifiers!!) {
            str += modifier.toString()
            str += ","
        }

        str = str.substring(0, str.length - 1)

        return str
    }

    private fun getDetectionLevelStr() : String {
        if (detectionLevel == null) {
            return "null"
        }

        return detectionLevel.toString()
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

        if (auUnitStr != "null" && auUnitStr != "") {
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
        //var engagementState : EngagementStateEnum? = null
        var disengagementOrdered : Boolean = false
        var attritionFromCombat : Int = 0
        var attackType : AttackTypeEnum? = null
        var riverCrossingType : RiverCrossingTypeEnum

        val nullVal = "null"

        if (properties.isNotEmpty()) {
            val unitID = properties[0]
            if (unitID != "") {
                unit = oob.unitIndex[unitID]
                if (unit == null) {
                    throw Exception("Couldn't find unit $unitID from oob")
                }
            }
        }

        if (properties.size != 9) {
            throw Exception("Properties size of unit string $str is not 9")
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

        val disengagementOrderedStr = properties[4]
        if (disengagementOrderedStr != nullVal) {
            disengagementOrdered = strToBool(disengagementOrderedStr)
        }

        val attritionFromCombatStr = properties[5]
        if (attritionFromCombatStr != nullVal) {
            attritionFromCombat = attritionFromCombatStr.toInt()
        }

        val attackTypeStr = properties[6]
        if (attackTypeStr != nullVal) {
            attackType = getAttackTypeFromString(attackTypeStr)
        }

        val riverCrossingTypeStr = properties[7]
        riverCrossingType = getRiverCrossingTypeFromString(riverCrossingTypeStr)

        val inPostureTransitionStr = properties[8]
        val inPostureTransition = strToBool(inPostureTransitionStr)

        return UnitState(unit, posture, attrition, commandState, disengagementOrdered, attritionFromCombat, attackType, riverCrossingType, inPostureTransition)

    }
}

class UnitState(
    unitInput : Unit?,
    postureInput: PostureEnum?,
    attrition: Int?,
    commandStateEnum: CommandStateEnum?,
    disengagementOrdered : Boolean, // as an ultimate hack we're also using this field to know if we're doing interdiction during bombardment or not :D
    attritionFromCombat : Int,
    attackTypeEnum: AttackTypeEnum?, // as an ultimate hack we're also using this field to know if the target is combat unit or support unit during bombardment :D :D
    riverCrossingTypeEnum: RiverCrossingTypeEnum, // as an ultimate hack we're also using this field to hold target bridge data: Prepared: Permanent, Hasty: Panel, None: None
    inPostureTransition: Boolean) { // as an ultimate hack we're also using this field to know if the target is soft or not during bombardment :D :D

    val unit = unitInput
    var posture = postureInput
    var attrition = attrition
    var commandState = commandStateEnum
    //var engagementState = engagementStateEnum
    var disengagementOrdered = disengagementOrdered
    var attritionFromCombat = attritionFromCombat
    var attackType = attackTypeEnum
    var riverCrossingType = riverCrossingTypeEnum
    var inPostureTransition = inPostureTransition

    fun inFullAssault() : Boolean {
        return posture == PostureEnum.FASL
    }

    fun isInfantryOutInTheOpen() : Boolean {
        if (unit?.eatsArmourInCity() == true || unit?.type == UnitTypeEnum.RECON) {
            if (posture != PostureEnum.DEF && posture != PostureEnum.RDEF && posture != PostureEnum.ADEF && posture != PostureEnum.SCRN) {
                return true
            }
        }

        return false
    }

    fun isScreenOrRec() : Boolean {
        return (posture == PostureEnum.SCRN || posture == PostureEnum.REC)
    }

    fun orderDisengagementAttempt() {
        disengagementOrdered = true
    }

    fun getStateString() : String {

        val nullVal = "-null"

        var unitStr = ""

        if (unit == null) {
            Log.d("DEBUG ", "3")
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

        unitStr += "-$disengagementOrdered"

        unitStr += "-$attritionFromCombat"

        unitStr += "-$attackType"

        unitStr += "-$riverCrossingType"

        unitStr += "-$inPostureTransition"

        Log.d("DEBUG ", "4")
        Log.d("DEBUG ", unitStr)

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

fun strToDay(str : String) : DayEnum {
    val hour = when (str) {
        "D0" -> DayEnum.D0
        "D1" -> DayEnum.D1
        "D2" -> DayEnum.D2
        "D3" -> DayEnum.D3
        "D4" -> DayEnum.D4
        else -> throw Exception("Unrecognized day: $str")
    }

    return hour
}

fun createConditions(str : String) : Conditions {
    val components = str.split("-")
    if (components.size != 6) {
        throw Exception("Conditions size is not 7")
    }

    val day = strToDay(components[0])
    val hour = strToHour(components[1])

    val fog = strToBool(components[2])
    val precipitation = strToBool(components[3])

    val chemUsageStartDayStr = components[4]
    val chemUsageStartHourStr = components[5]

    val chemUsageStartDay : DayEnum?
    val chemUsageStartHour : HourEnum?
    if (chemUsageStartDayStr == "null" || chemUsageStartHourStr == "null") {
        chemUsageStartDay = null
        chemUsageStartHour = null
    } else {
        chemUsageStartDay = strToDay(chemUsageStartDayStr)
        chemUsageStartHour = strToHour(chemUsageStartHourStr)
    }

    return Conditions(day, hour, fog, precipitation, chemUsageStartDay, chemUsageStartHour)

}

class Conditions(
    val dayEnum: DayEnum, val hourEnum: HourEnum, val fog : Boolean, val precipitation : Boolean,
    val chemUsageStartDay : DayEnum?, val chemUsageStartHour: HourEnum?) {

    fun doesPactUseChem() : Boolean {
        if (chemUsageStartDay != null && chemUsageStartHour != null) {
            return true
        }

        return false
    }

    fun getChemWarfareTurn() : Int? {
        val startDay = chemUsageStartDay ?: return null
        val startHour = chemUsageStartHour ?: return null

        val dayDiff = DayEnum.values().indexOf(dayEnum) - DayEnum.values().indexOf(startDay)
        val hourDiff = HourEnum.values().indexOf(hourEnum) - HourEnum.values().indexOf(startHour)

        return when {
            dayDiff > 0 -> dayDiff * 8 + hourDiff / 3 + 1
            dayDiff == 0 && hourDiff >= 0 -> hourDiff / 3 + 1
            else -> null
        }
    }

    fun getChemWarfareTurnForNato() : Int? {
        val waitTimeInTurnsAfterPactRelease = 25
        val pactChemTurn = getChemWarfareTurn() ?: return null
        return pactChemTurn - waitTimeInTurnsAfterPactRelease
    }

    fun toStateString() : String {
        return "$dayEnum-${hourEnum}-$fog-$precipitation-$chemUsageStartDay-$chemUsageStartHour"
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
        return "DAY: $dayEnum HOUR: $hourEnum FOG: $fog Precipitation: $precipitation, Chem usage started DAY: $chemUsageStartDay HOUR: $chemUsageStartHour"
    }

    fun isNight() : Boolean {
        return (hourEnum == HourEnum.H00 || hourEnum == HourEnum.H03 || hourEnum == HourEnum.H21)
    }

    fun isClear() : Boolean {
        return (!this.fog && !this.precipitation)
    }
}

