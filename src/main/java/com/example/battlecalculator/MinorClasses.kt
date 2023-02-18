package com.example.battlecalculator

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
    private val map : HashMap<FixedModifierEnum, Boolean> = initMap(list)

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
    private val map : HashMap<FixedModifierEnum, Int> = initMap()

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

        return map
    }
}
