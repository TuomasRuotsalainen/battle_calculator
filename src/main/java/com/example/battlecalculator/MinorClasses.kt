package com.example.battlecalculator

enum class AttackTypeEnum {
    HASTY, PREPARED
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
