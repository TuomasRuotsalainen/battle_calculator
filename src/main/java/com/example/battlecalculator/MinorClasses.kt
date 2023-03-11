package com.example.battlecalculator

import android.util.Log
import kotlin.collections.HashMap

enum class AttackTypeEnum {
    HASTY, PREPARED
}

enum class RiverCrossingTypeEnum {
    HASTY, PREPARED, NONE
}

class AttackType() {
    fun getMPCost(posture : PostureEnum, type : AttackTypeEnum) : Int {
        if (type == AttackTypeEnum.HASTY && posture == PostureEnum.MASL)    {
            return 3
        } else if (type == AttackTypeEnum.HASTY) {
            return 4
        } else if (type == AttackTypeEnum.PREPARED && posture == PostureEnum.MASL) {
            return 6
        }

        // Any other prepared assault
        return 8
    }

    fun getCombatModifier(type : AttackTypeEnum) : Int {
        if (type == AttackTypeEnum.PREPARED) {
            return 0
        }

        // Hasty assaults are more difficult
        return -2
    }
}

fun getEmptyActiveFixedModifiers() : ActiveFixedModifiers {
    return ActiveFixedModifiers(mutableListOf())
}

class ActiveFixedModifiers(list : MutableList<FixedModifierEnum>) {
    val map : HashMap<FixedModifierEnum, Boolean> = initMap(list)

    fun add(modifier : FixedModifierEnum) {
        map[modifier] = true
    }

    fun contains(modifier : FixedModifierEnum): Boolean {
        if (map[modifier] == true) {
            return true
        }

        return false
    }

    private fun initMap(list : MutableList<FixedModifierEnum>) : HashMap<FixedModifierEnum, Boolean> {
        val map = HashMap<FixedModifierEnum, Boolean>()

        for (listItem in list) {
            map[listItem] = true
        }

        return map
    }
}

class FixedModifiers() {
    val map : HashMap<FixedModifierEnum, Int> = initMap()

    fun getModifier(fixedModifierEnum: FixedModifierEnum): Int {
        return map[fixedModifierEnum]
            ?: throw Exception("Couldn't find fixed modifier for enum $fixedModifierEnum")
    }

    private fun initMap() : HashMap<FixedModifierEnum, Int> {
        val map = HashMap<FixedModifierEnum, Int>()
        map[FixedModifierEnum.NATO_DEFENDS_MULTI_COUNTRY] = 2
        map[FixedModifierEnum.DEFENDER_RESTING] = 3
        map[FixedModifierEnum.DEFENDER_ENGAGED] = 2
        map[FixedModifierEnum.DEFENDER_HALF_ENGAGED] = 1
        map[FixedModifierEnum.DEFENDER_DELAYED] = 3
        map[FixedModifierEnum.DEFENDER_OUT_OF_COMMAND] = 3
        map[FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC] = 1
        map[FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND] = -2
        map[FixedModifierEnum.PACT_ATTACKING_REAR] = -3
        map[FixedModifierEnum.PACT_DEFENDING_REAR] = 3
        map[FixedModifierEnum.ATTACKER_HALF_ENGAGED] = 2
        map[FixedModifierEnum.ATTACKER_OUT_OF_COMMAND] = -4
        map[FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC] = -2
        map[FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND] = 2
        map[FixedModifierEnum.ATTACKER_USES_REC] = 2
        map[FixedModifierEnum.ATTACKER_USES_SAPPERS] = 2
        map[FixedModifierEnum.DEFENDER_USES_SAPPERS] = -1

        return map
    }
}

class CombatSupportSelection() {
    private var attackerSupport : CombatSupport? = null
    private var defenderSupport : CombatSupport? = null

    fun isAirBeingUsed() : Boolean {
        if (attackerSupport == null || defenderSupport == null) {
            throw Exception("This method shouldn't be called when CS isn't defined")
        }

        return (attackerSupport!!.getAirPoints() != 0 || attackerSupport!!.getHelicopterCount() != 0 || defenderSupport!!.getAirPoints() != 0 || defenderSupport!!.getHelicopterCount() != 0)
    }

    fun setAttackerCombatSupport(support : CombatSupport) {
        attackerSupport = support
    }

    fun setDefenderCombatSupport(support : CombatSupport) {
        defenderSupport = support
    }

    fun getAttackerCombatSupport() : CombatSupport? {
        return attackerSupport
    }

    fun getDefenderCombatSupport() : CombatSupport? {
        return defenderSupport
    }

    fun toStateString() : String {
        var str = ""
        if (attackerSupport != null) {
            str += attackerSupport!!.toStateString()
        }

        if (defenderSupport != null) {
            if (str.isNotEmpty()) {
                str += "&"
            }
            str += defenderSupport!!.toStateString()
        }

        return str
    }
}

fun parseCombatSupport(str : String) : CombatSupport {
    // DEF-2-2,3,0-2-true-2
    // AT-0-0,0,0-5-false-0

    val isAttacker : Boolean
    val artilleryPoints : Int
    val helicopterPoints : MutableList<Int> = mutableListOf()
    val airPoints : Int
    val insideCAS : Boolean

    val contents = str.split("-")
    if (contents.size != 6) {
        throw Exception("Bad contents length for combat support string $str")
    }

    isAttacker = if (contents[0] == "DEF") {
        false
    } else if (contents[0] == "AT") {
        true
    } else {
        throw Exception("Bad selection for contents[0] for string $str")
    }

    artilleryPoints = contents[1].toIntOrNull()!!

    val helicopterPointsList = contents[2].split(",")
    for (heliPoints in helicopterPointsList) {
        val oneHeli = heliPoints.toIntOrNull()
            ?: throw Exception("Couldn't convert oneHeli to int for string  $str")

        helicopterPoints.add(oneHeli)
    }

    if (helicopterPoints.size != 3) {
        throw Exception("Helicopter points size is not 3 for string  $str")
    }

    airPoints = contents[3].toIntOrNull()!!

    val insideCASStr = contents[4]
    insideCAS = if (insideCASStr == "true") {
        true
    } else if (insideCASStr == "false") {
        false
    } else {
        throw Exception("Couldn't convert inside CAS string to bool for string  $str")
    }

    val ewPoints = contents[5].toIntOrNull()!!


    return CombatSupport(artilleryPoints, airPoints, helicopterPoints, insideCAS, isAttacker, ewPoints)

}

class CombatSupport(val artilleryPoints: Int, private var airPoints : Int, var helicopterPoints : List<Int>, val targetInCASZone : Boolean, val isAttacker : Boolean, val ewPoints : Int) {

    fun getHelicopterCount() : Int {
        var counter = 0
        for (heliPoint in helicopterPoints) {
            if (heliPoint > 0) {
                counter += 1
            }
        }

        return counter
    }

    fun getAirPoints() : Int {
        return airPoints
    }

    fun adjustAirPoints(aaResult : Tables.AAFire.Result) {
        val aaDecrease = aaResult.getAbortedAirPoints() + aaResult.getShotDownAirPoints()
        if (aaDecrease > airPoints) {
            airPoints = 0
        }
        airPoints -= aaDecrease
    }

    fun adjustHelicopterPoints(shotDownHelicopterIndex: Int) {
        if (shotDownHelicopterIndex < 0) {
            throw Exception("shotDownHelicopterIndex is less than 0")
        }
        helicopterPoints = helicopterPoints.mapIndexed { index, value ->
            if (index == shotDownHelicopterIndex) 0 else value
        }
    }

    fun toStateString() : String {
        //AT-0-0,0,0-5-false-2
        var str = ""

        Log.d("DEBUG", str)

        str += if (isAttacker) {
            "AT"
        } else {
            "DEF"
        }

        Log.d("DEBUG", str)

        str += "-$artilleryPoints"

        Log.d("DEBUG", str)

        str += "-"
        Log.d("DEBUG", str)
        for (heliPoint in helicopterPoints) {
            str += "$heliPoint,"
            Log.d("DEBUG", str)
        }
        Log.d("DEBUG", str)
        str = str.dropLast(1)
        Log.d("DEBUG", str)
        str += "-$airPoints"
        Log.d("DEBUG", str)
        str += if (targetInCASZone) {
            "-true"
        } else {
            "-false"
        }
        Log.d("DEBUG", str)

        str +="-$ewPoints"

        Log.d("DEBUG", str)
        Log.d("DEBUG", "Parsed combat support string: $str")

        val contents = str.split("-")
        if (contents.size != 6) {
            Log.d("artilleryPoints", "$artilleryPoints")
            Log.d("airPoints", "$airPoints")
            Log.d("artilleryPoints", "$artilleryPoints")
            Log.d("helicopterPoints", "$helicopterPoints")
            Log.d("targetInCASZone", "$targetInCASZone")
            Log.d("isAttacker", "$isAttacker")
            Log.d("ewPoints", "$ewPoints")
            throw Exception("Bad contents length for combat support string $str. ")
        }



        return str
    }

    fun getTotalSupport(): Int {
        val airPointTotal : Int = if (targetInCASZone) {
            airPoints * 2
        } else {
            airPoints
        }

        return artilleryPoints + helicopterPoints.sum() + airPointTotal
    }
}

class DieRoll() {
    private var result : Int = roll()

    fun getResultWithModifiers() : Int {
        return result
    }

    fun getResultWithoutModifiers() : Int {
        return result
    }

    // This will give results from 1 to 10
    private fun roll(): Int {
        return (1..10).random()
    }
}
