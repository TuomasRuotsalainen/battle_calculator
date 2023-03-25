package com.example.battlecalculator

class Calculator() {
    fun getInitialAttackDifferential(unit : Unit, posture : PostureEnum, attackTypeEnum: AttackTypeEnum): Int? {
        val postures = Postures()
        // Note, that defender type influences this as well
        val unitPosture = postures.getPosture(posture)
        if (unitPosture.attack == null) {
            return null
        }

        val attackTypeDifferential = AttackType().getCombatModifier(attackTypeEnum)

        return unitPosture.attack + unit.attack + attackTypeDifferential
    }


    fun getInitialDefenseDifferential(unit : Unit, posture : PostureEnum): Int {
        val postures = Postures()
        return unit.defense + (-postures.getPosture(posture).defense)
    }

    fun calculateCurrentCombatDifferential(state: GameState): Pair<Int,String> {
        var explanation = ""
        if (state.attackingUnit == null || state.defendingUnits.size == 0) {
            throw Exception("Attacking or defending units not defined")
        }

        if (state.hexTerrain == null) {
            throw Exception("Calculator: hexTerrain is null")
        }

        return calculateInitialDifferential(state)

    }

    private fun calculateInitialDifferential(state : GameState): Pair<Int, String> {
        var explanation = ""

        val attacker: UnitState = state.attackingUnit!!
        val defenders = state.defendingUnits

        val attackerCombatStrength =
            calculateCombatStrength(attacker, defenders, state.hexTerrain!!, true)

        explanation += "Attacker combat strength $attackerCombatStrength (considering defender type and terrain)\n"

        var defenderCombatStrength = 0
        for (defender in defenders) {
            defenderCombatStrength = +calculateCombatStrength(
                defender,
                mutableListOf(state.attackingUnit!!),
                state.hexTerrain!!,
                false
            )
        }

        explanation += "Defender combat strength: $defenderCombatStrength (considering attacker type and terrain)\n"

        val postures = Postures()

        val defenderPostures = defenders.map { it.posture!! }

        val attackerPostureModifier = postures.getCombatModifier(attacker.posture!!, true)
        val defenderPostureModifier = postures.getLeastFavourableDefenseModifier(defenderPostures)

        if (attackerPostureModifier == null) {
            throw Exception("Tried to use bad posture for attacking: ${attacker.posture.toString()}")
        }

        explanation += "Attacker posture: $attackerPostureModifier, defender posture: $defenderPostureModifier\n"

        val totalCombatDifferential = defenderCombatStrength - attackerCombatStrength
        val combatDifferentialAfterPostures =
            totalCombatDifferential + attackerPostureModifier + defenderPostureModifier

        val attackTypeEnum = state.attackType!!
        val attackTypeModifier = AttackType().getCombatModifier(attackTypeEnum)

        explanation += "Attack type: $attackTypeModifier\n"

        if (state.hexTerrain == null) {
            throw Exception("Hexterrain is null when calculating results")
        }

        val terrainCombatModifier = Tables.TerrainCombatTable().getCombatModifier(state.hexTerrain!!, state.riverCrossingType!!, defenderPostures)
        explanation += terrainCombatModifier.second

        val fixedModifiers = calculateFixedModifiers(state)
        explanation += fixedModifiers.second

        return Pair(combatDifferentialAfterPostures + attackTypeModifier + terrainCombatModifier.first + fixedModifiers.first, explanation)
    }

    private fun calculateFixedModifiers(state : GameState): Pair<Int, String> {
        var explanation = ""
        var cadreModifier = 0
        var defensiveWorksModifier = 0
        var attritionModifier = 0

        val attacker: UnitState = state.attackingUnit!!

        var defenderMinCadre : Int? = null
        for (defendingUnit in state.defendingUnits) {
            if (defenderMinCadre == null) {
                defenderMinCadre = defendingUnit.unit!!.cadre
                continue
            }

            if (defendingUnit.unit!!.cadre < defenderMinCadre) {
                defenderMinCadre = defendingUnit.unit.cadre
            }
        }

        if (defenderMinCadre != null) {
            cadreModifier = defenderMinCadre - attacker.unit!!.cadre
        } else {
            throw Exception("Defender cadre is null")
        }

        if (state.hexTerrain != null) {
            defensiveWorksModifier = state.hexTerrain!!.getDefensiveWorksCombatModifier()
            if (defensiveWorksModifier > 0) {
                explanation += "Defensive works: $defensiveWorksModifier\n"
            }
        }

        val attackerAttrition = state.attackingUnit!!.attrition!!
        var defenderTotalAttrition = 0
        for (defender in state.defendingUnits) {
            defenderTotalAttrition += defender.attrition!!
        }

        attritionModifier = defenderTotalAttrition - attackerAttrition
        if (attritionModifier > 0) {
            explanation += "Attrition difference: $attritionModifier\n"
        }

        val fixedModifiers = FixedModifiers()

        var fixedModifierCounter = 0
        for (fixedModifier in FixedModifierEnum.values()) {
            if (state.activeFixedModifiers.contains(fixedModifier)) {
                val modifier = fixedModifiers.getModifier(fixedModifier)
                fixedModifierCounter += modifier
                explanation += "${fixedModifier.name}: $modifier\n"
            }
        }

        var adjacencyModifier = 0
        if (state.adjacentAttackerCount != null) {
            adjacencyModifier += state.adjacentAttackerCount!! * 2
        }

        if (state.adjacentDefenderCount != null) {
            adjacencyModifier += state.adjacentDefenderCount!! * -1
        }

        explanation += "Adjacent units total: $adjacencyModifier\n"

        explanation += "Cadre difference: $cadreModifier\n"
        val total = cadreModifier + defensiveWorksModifier + attritionModifier + adjacencyModifier + fixedModifierCounter

        return Pair(total, explanation)
    }

    private fun calculateCommandStateDifferential(unitInQuestion : UnitState, isAttacker : Boolean, gameState: GameState): Int {
        if (unitInQuestion.unit == null) {
            throw Exception("calculateCommandStateDifferential: unit is null")
        }

        if (unitInQuestion.commandState == null) {
            throw Exception("calculateCommandStateDifferential: commandState is null")
        }

        if (isAttacker) {

        }

        // TODO finish this

        return 0
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