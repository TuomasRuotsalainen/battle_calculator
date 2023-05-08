package com.example.battlecalculator

class Calculator() {

    fun getInitialDifferential(unitState : UnitState): Pair<Int?, String> {
        var explanation = "Initial combat differential:\n"

        val postures = Postures()
        val fixedModifiers = FixedModifiers()
        // Note, that defender type influences this as well
        val unitPosture = postures.getPosture(unitState.posture!!)
        if (unitPosture.attack == null && unitState.attackType != null) {
            return Pair(null, "A unit can't attack with this posture!")
        }

        val fightingValue : Int
        val postureDifferential : Int
        var mpExplanation = ""
        var differential = 0

        if (unitState.attackType == null) {
            fightingValue = unitState.unit!!.defense
            postureDifferential = unitPosture.defense
        } else {
            fightingValue = unitState.unit!!.attack
            val attackTypeModifier = AttackType().getCombatModifier(unitState.attackType!!)
            explanation += "$attackTypeModifier (attack type) "
            postureDifferential = unitPosture.attack!!
            differential += attackTypeModifier
            mpExplanation = "\n\nMP cost: ${AttackType().getMPCost(unitPosture.enum, unitState.attackType!!)}"

        }

        val commandStateDifferential = fixedModifiers.getCommandStateModifier(unitState)

        differential += fightingValue

        explanation += "+ $postureDifferential (posture) + $fightingValue (unit)"
        explanation += " - ${unitState.attrition} (attrition)"

        differential += postureDifferential
        differential -= unitState.attrition!!

        if (commandStateDifferential != 0) {
            differential += commandStateDifferential
            explanation += "\n+ $commandStateDifferential (command state)"
        }


        explanation += "\n Total = $differential"

        explanation += mpExplanation

        return Pair(differential, explanation)
    }

    /*
    fun getInitialDefenseDifferential(unit : Unit, posture : PostureEnum): Int {
        val postures = Postures()
        return unit.defense + (-postures.getPosture(posture).defense)
    }


    fun calculateUnitsDifferential(unitState: UnitState, attackTypeEnum: AttackTypeEnum?, commandStateEnum: CommandStateEnum): Pair<Int, String> {

        val posture = Postures().getPosture(unitState.posture!!)

        var explanation = ""
        val differential : Int?

        if (attackTypeEnum != null) {
            if (posture.attack == null) {
                throw Exception("Unit with posture ${posture.enum} is not able to conduct attacks!")
            }

            val attackType = AttackType()

            // TODO should we add command state here as well?
            differential = this.getInitialDifferential(unitState.unit!!, unitState.posture!!, attackTypeEnum)

            if (differential == null) {
                throw Exception("Tried to calculate attack differential for posture ${posture}")
            }

            explanation = "Initial attack differential:\n${unitState.unit.attack} (unit) + ${posture.attack} (posture) + ${
                attackType.getCombatModifier(
                    attackTypeEnum
                )
            } (attack type)\n=$differential \n\nMP cost: ${attackType.getMPCost(posture.enum, attackTypeEnum)} (prepared assault)"
        } else {
            differential = this.getInitialDefenseDifferential(unitState.unit!!, posture.enum)
            explanation = "Initial defense differential:\n${unitState.unit.defense} (unit) + ${posture.defense} (posture) \n=$differential"
        }

        return Pair(differential, explanation)
    }*/

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

        if (state.hexTerrain == null) {
            throw Exception("Hexterrain is null when calculating results")
        }

        // 15.6.5 Terrain
        val terrainCombatModifier = Tables.TerrainCombatTable().getCombatModifier(state.hexTerrain!!, state.riverCrossingType!!, defenderPostures)
        explanation += terrainCombatModifier.second

        val fixedModifiers = calculateFixedModifiers(state)
        explanation += fixedModifiers.second

        return Pair(combatDifferentialAfterPostures + terrainCombatModifier.first + fixedModifiers.first, explanation)
    }

    private fun calculateFixedModifiers(state : GameState): Pair<Int, String> {
        var explanation = ""
        var cadreModifier = 0
        var defensiveWorksModifier = 0
        var attritionModifier = 0

        val attacker: UnitState = state.attackingUnit!!

        // 15.6.2 Attack type
        val attackTypeEnum = state.attackType!!
        val attackTypeModifier = AttackType().getCombatModifier(attackTypeEnum)
        explanation += "Attack type: $attackTypeModifier\n"

        // 15.6.3 Cadre rating
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
            explanation += "Cadre difference: $cadreModifier\n"
            cadreModifier = defenderMinCadre - attacker.unit!!.cadre
        } else {
            throw Exception("Defender cadre is null")
        }

        // 15.6.4 Nationality
        // 15.6.8 Resting
        // 15.6.12. Sappers (WP Only)
        // 15.6.14 Engagement
        // 15.6.15 Delay
        // 15.6.20 WP Advance Axis
        // 15.6.18 Out of Command
        // 15.6.19 Front-Line command
        val fixedModifiers = FixedModifiers()
        var fixedModifierCounter = 0
        for (fixedModifier in FixedModifierEnum.values()) {
            if (state.activeFixedModifiers.contains(fixedModifier)) {
                val modifier = fixedModifiers.getModifier(fixedModifier)
                fixedModifierCounter += modifier
                explanation += "${fixedModifier.name}: $modifier\n"
            }
        }

        // Terrain is in other function

        // 15.6.6 Defensive works
        if (state.hexTerrain != null) {
            defensiveWorksModifier = state.hexTerrain!!.getDefensiveWorksCombatModifier()
            if (defensiveWorksModifier < 0) {
                explanation += "Defensive works: $defensiveWorksModifier\n"
                if (state.activeFixedModifiers.contains(FixedModifierEnum.ATTACKER_HAS_SAPPERS)) {
                    val reductionValue = 2
                    explanation += "Attacker's sappers defensive works reduction: $reductionValue\n"
                    defensiveWorksModifier += reductionValue
                }
            }
        }

        // 15.6.7 Attrition
        val attackerAttrition = state.attackingUnit!!.attrition!!
        var defenderTotalAttrition = 0
        for (defender in state.defendingUnits) {
            defenderTotalAttrition += defender.attrition!!
        }

        attritionModifier = defenderTotalAttrition - attackerAttrition
        if (attritionModifier > 0) {
            explanation += "Attrition difference: $attritionModifier\n"
        }

        // 15.6.9. Electronic warfare (only combat modifiers)
        var ewModifier = 0
        if (state.combatSupport != null) {
            val ewModifiers = state.combatSupport!!.getEwModifiers()
            var ewExplanation = ""
            if (ewModifiers.first != null) {
                ewExplanation += "Attacker EW: ${ewModifiers.first}"
                ewModifier += ewModifiers.first!!
            }

            if (ewModifiers.second != null) {
                ewExplanation += "Defender EW: ${ewModifiers.second}"
                ewModifier += ewModifiers.second!!
            }

            if (ewExplanation != "") {
                explanation += ewExplanation + "\n"
            }
        }

        // Combat support is calculated in CombatResolutionActivity

        // 15.6.13 Adjacent Units
        var adjacencyModifier = 0
        if (state.adjacentAttackerCount != null) {
            adjacencyModifier += state.adjacentAttackerCount!! * 2
        }

        if (state.adjacentDefenderCount != null) {
            adjacencyModifier += state.adjacentDefenderCount!! * -1
        }

        // 15.6.16 Weather
        var weatherModifier = 0
        if (state.conditions.isNight()) {
            if (state.activeAlliance == Alliances.NATO) {
                val natoAttackingNight = 2
                explanation += "NATO attacking at night: $natoAttackingNight\n"
                weatherModifier += natoAttackingNight
            } else {
                val pactAttackingNight = -2
                explanation += "PACT attacking at night: $pactAttackingNight\n"
                weatherModifier += pactAttackingNight
            }
        }

        if (!state.conditions.isClear()) {
            if (state.activeAlliance == Alliances.NATO) {
                val natoAttackingFogRain = 1
                explanation += "NATO attacking during rain or fog: $natoAttackingFogRain\n"
                weatherModifier += natoAttackingFogRain
            } else {
                val pactAttackingFogRain = -1
                explanation += "PACT attacking during rain or fog: $pactAttackingFogRain\n"
                weatherModifier += pactAttackingFogRain
            }
        }

        // 15.6.17 Chemical Warfare
        val chemTurn = state.conditions.getChemWarfareTurn()
        var chemModifier = 0
        if (chemTurn != null) {
            if (state.conditions.doesPactUseChem()) {
                var modifier = when (chemTurn) {
                    in 1..8 -> 3
                    in 9..16 -> 2
                    in 17.. 100 -> 1
                    else -> throw Exception("Unrecognized turn")
                }

                if (state.activeAlliance == Alliances.NATO) {
                    modifier = -modifier
                }

                explanation += "PACT using chemical weapons (turn $chemTurn): $modifier\n"
                chemModifier += modifier
            }

            val natoChemTurn = state.conditions.getChemWarfareTurnForNato()
            if (natoChemTurn != null) {
                if (natoChemTurn > 0) {
                    var modifier = when (natoChemTurn) {
                        in 1..8 -> 2
                        in 9..100 -> 1
                        else -> throw Exception("Unrecognized turn")
                    }

                    if (state.activeAlliance == Alliances.PACT) {
                        modifier = -modifier
                    }

                    explanation += "NATO using chemical weapons (turn $chemTurn): $modifier\n"
                    chemModifier += modifier
                }
            }

        }

        // 15.6.18 Out of Command
        // 15.6.19 Front-Line command
        /*val attackerCommandState = state.getCommandState(true)
        val defenderCommandState = state.getCommandState(false)

        val attackerCommandStateModifier = when (attackerCommandState.first) {
            CommandStateEnum.NORMAL -> 0
            CommandStateEnum.FRONT_LINE_COMMAND -> {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.ATTACKER_FRONT_LINE_COMMAND)
                explanation += "Attacker using front line command: $modifier\n"
                modifier
            }
            CommandStateEnum.OUT_OF_COMMAND -> if (attackerCommandState.second) {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND_SCREEN_REC)
                explanation += "Attacker screen/rec out of command: $modifier\n"
                modifier
            } else {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.ATTACKER_OUT_OF_COMMAND)
                explanation += "Attacker out of command: $modifier\n"
                modifier
            }
        }

        val defenderCommandStateModifier = when (defenderCommandState.first) {
            CommandStateEnum.NORMAL -> 0
            CommandStateEnum.FRONT_LINE_COMMAND -> {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.DEFENDER_FRONT_LINE_COMMAND)
                explanation += "Defender using front line command: $modifier\n"
                modifier
            }
            CommandStateEnum.OUT_OF_COMMAND -> if (defenderCommandState.second) {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND_SCREEN_REC)
                explanation += "Defender screen/rec out of command: $modifier\n"
                modifier
            } else {
                val modifier = fixedModifiers.getModifier(FixedModifierEnum.DEFENDER_OUT_OF_COMMAND)
                explanation += "Defender out of command: $modifier\n"
                modifier
            }
        }

        val totalCommandModifier = attackerCommandStateModifier + defenderCommandStateModifier
        */

        explanation += "Adjacent units total: $adjacencyModifier\n"

        val total = cadreModifier + defensiveWorksModifier + attritionModifier + adjacencyModifier + fixedModifierCounter + attackTypeModifier + weatherModifier + chemModifier

        return Pair(total, explanation)
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