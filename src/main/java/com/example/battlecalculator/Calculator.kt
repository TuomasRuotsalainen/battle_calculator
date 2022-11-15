package com.example.battlecalculator

import android.util.Log

class Calculator(private val postures: Postures) {
    fun getInitialAttackDifferential(unit : Unit, posture : PostureEnum, attackTypeEnum: AttackTypeEnum): Int? {
        // Note, that defender type influences this as well
        val unitPosture = postures.getPosture(posture)
        if (unitPosture.attack == null) {
            return null
        }

        val attackTypeDifferential = AttackType().getCombatModifier(attackTypeEnum)

        //Log.d("TUOMAS TAG", "Posture: ${unitPosture.attack}, unit attack value: ${unit.attack}, attack type modifier: $attackTypeDifferential")

        return unitPosture.attack + unit.attack + attackTypeDifferential
    }


    fun getInitialDefenseDifferential(unit : Unit, posture : PostureEnum): Int {
        return unit.defense + (-postures.getPosture(posture).defense)
    }
}