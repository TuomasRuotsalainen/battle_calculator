package com.example.battlecalculator

import android.util.Log
import kotlin.collections.HashMap
import kotlin.random.Random

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

// https://boardgamegeek.com/image/5942891/hanbarca
fun getMovementType(unitTypeEnum: UnitTypeEnum) : MovementTypeEnum {
    return when (unitTypeEnum) {
        UnitTypeEnum.INFANTRY -> MovementTypeEnum.FOOT
        UnitTypeEnum.MOTORIZED -> MovementTypeEnum.MOTORIZED
        UnitTypeEnum.TOWED_ARTILLERY -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.HELICOPTER -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.ARMOR -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.MECHANIZED -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.OTHER_ARTILLERY -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.RECON -> MovementTypeEnum.MECHANIZED
        UnitTypeEnum.HQ -> MovementTypeEnum.MECHANIZED
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

    fun sappersInUse(isAttacker: Boolean) : Boolean {
        return if (isAttacker) {
            map.contains(FixedModifierEnum.ATTACKER_USES_SAPPERS)
        } else {
            map.contains(FixedModifierEnum.DEFENDER_USES_SAPPERS)
        }
    }

    fun remove(modifier: FixedModifierEnum) {
        map[modifier] = false
    }

    // EW Effects 24.4.2 E3
    fun applyEW(attackerEWResult: Tables.EWResult, defenderEWResult: Tables.EWResult) {
        if (attackerEWResult.effects.contains(EwEffectEnum.ENEMY_COMMAND_DISRUPTED)) {
            this.add(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND)
            this.add(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC)
            this.remove(FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND)
        }

        if (defenderEWResult.effects.contains(EwEffectEnum.ENEMY_COMMAND_DISRUPTED)) {
            this.add(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND)
            this.add(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC)
            this.remove(FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND)
        }
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
        map[FixedModifierEnum.ATTACKER_HALF_ENGAGED] = -2
        map[FixedModifierEnum.ATTACKER_OUT_OF_COMMAND] = -4
        map[FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC] = -2
        map[FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND] = 2
        map[FixedModifierEnum.ATTACKER_USES_REC] = 2
        map[FixedModifierEnum.ATTACKER_USES_SAPPERS] = 2
        map[FixedModifierEnum.ATTACKER_HAS_SAPPERS] = 0
        map[FixedModifierEnum.DEFENDER_USES_SAPPERS] = -1
        map[FixedModifierEnum.DEFENDER_HAS_SAPPERS] = 0

        return map
    }
}

class CombatSupportSelection() {
    private var attackerSupport : CombatSupport? = null
    private var defenderSupport : CombatSupport? = null

    private var attackerEwModifier : Int? = null
    private var defenderEwModifier : Int? = null

    fun getEwModifiers(): Pair<Int?, Int?> {
        return Pair(attackerEwModifier, defenderEwModifier)
    }

    fun ewInUse() : Boolean {
        return (attackerSupport!!.getEWPoints()!! > 0) || (defenderSupport!!.getEWPoints()!! > 0)
    }

    fun applyEwResults(attackerEWResult: Tables.EWResult, defenderEWResult: Tables.EWResult) {
        this.attackerEwModifier = applyEwModifier(attackerEWResult, true)
        this.defenderEwModifier = applyEwModifier(defenderEWResult, false)

        this.attackerSupport!!.adjustCombatSupport(defenderEWResult)
        this.defenderSupport!!.adjustCombatSupport(attackerEWResult)
    }

    private fun applyEwModifier(ewResult: Tables.EWResult, isAttacker: Boolean): Int {
        return if (isAttacker) {
            ewResult.combatModifier.first
        } else {
            ewResult.combatModifier.second
        }
    }

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

const val DELIMITER = "#"

fun parseCombatSupport(str : String) : CombatSupport {
    // DEF-2-2,3,0-2-true-2,0
    // AT-0-0,0,0-5-false-0,1

    val isAttacker : Boolean
    val artilleryPoints : Int
    val helicopterPoints : MutableList<Int> = mutableListOf()
    val airPoints : Int
    val insideCAS : Boolean

    val contents = str.split(DELIMITER)
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

    val ewPoints : Int?
    val ewDieModifier : Int?
    val ewValues = contents[5]
    if (ewValues == "null") {
        ewPoints = null
        ewDieModifier = null
    } else {
        val values = ewValues.split(",")
        if (values.size != 2) {
            throw Exception("Incorrect EW value length encountered for string $str")
        }
        ewPoints = values[0].toIntOrNull()
        ewDieModifier = values[1].toIntOrNull()

        if (ewDieModifier == null || ewPoints == null) {
            throw Exception("Encountered null first or second num for string $str")
        }
    }


    return CombatSupport(artilleryPoints, airPoints, helicopterPoints, insideCAS, isAttacker, ewPoints, ewDieModifier)

}

class CombatSupport(private var artilleryPoints: Int, private var airPoints : Int, private var helicopterPoints : MutableList<Int>, val targetInCASZone : Boolean, val isAttacker : Boolean, private var ewPoints: Int?, private var ewRollModifier: Int?) {

    //private var reductionByEnemyEW : Int? = null

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

    // Electronic warfare 15.6.9
    // EW Effects 24.4.2 E1 E2 E3
    fun adjustCombatSupport(enemyEwResult : Tables.EWResult) {
        /*val modifier = if (isAttacker) {
            // If adjusting attacker's combat support, we're talking about defender EW result and vice versa
                enemyEwResult.combatModifier.second
            } else {
                enemyEwResult.combatModifier.first
            }

        reductionByEnemyEW = modifier*/

        for (effect in enemyEwResult.effects) {
            when (effect) {
                EwEffectEnum.ENEMY_ARTILLERY_HALVED -> {
                    this.artilleryPoints = artilleryPoints/2 // This rounds it down
                }
                EwEffectEnum.ENEMY_AVIATION_HALVED -> {
                    this.airPoints = airPoints/2 // This rounds it down
                    for (i in 0 until this.helicopterPoints.size)
                        this.helicopterPoints[i] = this.helicopterPoints[i]/2 // This rounds it down
                }
                else -> {}
            }
        }

    }

    fun setEW(newEwPoints : Int, rollModifier : Int) {
        this.ewRollModifier = rollModifier
        this.ewPoints = newEwPoints
    }

    fun getEWRollModifier() : Int? {
        return ewRollModifier
    }

    fun getEWPoints() : Int? {
        return ewPoints
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
        } as MutableList<Int>
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

        str += DELIMITER+"$artilleryPoints"

        Log.d("DEBUG", str)

        str += DELIMITER
        Log.d("DEBUG", str)
        for (heliPoint in helicopterPoints) {
            str += "$heliPoint,"
            Log.d("DEBUG", str)
        }
        Log.d("DEBUG", str)
        str = str.dropLast(1)
        Log.d("DEBUG", str)
        str += DELIMITER+"$airPoints"
        Log.d("DEBUG", str)
        str += if (targetInCASZone) {
            DELIMITER+"true"
        } else {
            DELIMITER+"false"
        }
        Log.d("DEBUG", str)

        if (ewPoints == null) {
            str +=DELIMITER+"null"
        } else {
            if (ewRollModifier == null) {
                throw Exception("ewRollModifier shouldn't be null when ewPoints is not null.")
            }
            str += DELIMITER+"${ewPoints!!}"
            str += ",${ewRollModifier!!}"
        }

        Log.d("DEBUG", str)
        Log.d("DEBUG", "Parsed combat support string: $str")

        val contents = str.split(DELIMITER)
        if (contents.size != 6) {
            Log.d("artilleryPoints", "$artilleryPoints")
            Log.d("airPoints", "$airPoints")
            Log.d("artilleryPoints", "$artilleryPoints")
            Log.d("helicopterPoints", "$helicopterPoints")
            Log.d("targetInCASZone", "$targetInCASZone")
            Log.d("isAttacker", "$isAttacker")
            Log.d("ewPoints", "$ewPoints")
            Log.d("ewRollModifier", "$ewRollModifier")
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

class Dice() {
    val seed = System.currentTimeMillis()
    val random = Random(seed)

    fun roll() : DiceRollResult {
        val result = (1..10).random(random)
        return DiceRollResult(result)
    }
}

class DiceRollResult(result : Int) {
    private var validResult : Int = validate(result)

    private fun validate(result: Int) : Int {
        if (result in 1..10) {
            return result
        } else {
            throw IllegalArgumentException("Value must be between 1 and 10")
        }
    }

    fun get(): Int {
        return validResult
    }
}
