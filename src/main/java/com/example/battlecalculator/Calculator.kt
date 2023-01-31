package com.example.battlecalculator

import android.content.Intent
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

    fun calculateCurrentCombatDifferential(state : GameState): Int {
        if (state.attackingUnit == null || state.defendingUnits.size == 0) {
            throw Exception("Attacking or defending units not defined")
        }

        val attacker : UnitState = state.attackingUnit!!
        val defenders = state.defendingUnits

        val attackerCombatStrength = calculateCombatStrength(attacker, defenders, state.hexTerrain!!, true)
        var defenderCombatStrength = 0
        for (defender in defenders) {
            defenderCombatStrength =+ calculateCombatStrength(defender, mutableListOf(state.attackingUnit!!), state.hexTerrain!!, false)
        }

        val postures = Postures()

        val defenderPostures = defenders.map { it.posture!! }

        val attackerPostureModifier = postures.getCombatModifier(attacker.posture!!, true)
        val defenderPostureModifier = postures.getLeastFavourableDefenseModifier(defenderPostures)

        if (attackerPostureModifier == null) {
            throw Exception("Tried to use bad posture for attacking: ${attacker.posture.toString()}")
        }

        val totalCombatDifferential = defenderCombatStrength - attackerCombatStrength
        val combatDifferentialAfterPostures = totalCombatDifferential + attackerPostureModifier + defenderPostureModifier

        val attackTypeEnum = state.attackType!!
        val attackTypeModifier = AttackType().getCombatModifier(attackTypeEnum)



        return combatDifferentialAfterPostures
    }

    private fun calculateFixedModifiers(state : GameState): (Int, List<String>) {

    }

    private fun calculateCombatStrength(unitInQuestion : UnitState, opposingUnits : List<UnitState>, terrain : HexTerrain, isAttacking : Boolean) : Int {
        if (unitInQuestion.unit?.type == UnitTypeEnum.ARMOR) {
            if (!isAttacking) {
                if (terrain.isPlainsOnly()) {
                    return unitInQuestion.unit.attack
                }
            } else {
                if (terrain.features[TerrainEnum.CITY] == true) {
                    for (opposingUnit in opposingUnits) {
                        if (opposingUnit.unit?.eatsArmourInCity() == true) {
                            return unitInQuestion.unit.defense
                        }
                    }
                }
            }
        }

        if (!isAttacking) {
            if (terrain.isPlainsOnly()) {
                if (unitInQuestion.isInfantryOutInTheOpen()) {
                    for (opposingUnit in opposingUnits) {
                        if (opposingUnit.unit?.type == UnitTypeEnum.ARMOR) {
                            return unitInQuestion.unit?.attack!!
                        }
                    }
                }
            }
        }

        if (isAttacking) {
            return unitInQuestion.unit?.attack!!
        }

        return unitInQuestion.unit?.defense!!
    }
}