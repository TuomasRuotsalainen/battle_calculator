package com.example.battlecalculator


class Tables {

    private class DisengagementResultRangeCell(f1upperLimit: Int, s0lowerLimit: Int) {
        var f1upperLimit: Int = f1upperLimit
        var s0lowerLimit: Int = s0lowerLimit

    }

    class DisengagementTable() {

        private val disengagementTable = getDisengagementTable()

        // 15.8.7 - 15.8.10
        fun getEmergencyPostureAndAttritionForRetreat(currentPosture: PostureEnum, inPostureTransition: Boolean) : Pair<PostureEnum, Int> {
            if (inPostureTransition) {
                return Pair(PostureEnum.TAC, 0)
            }

            return when (currentPosture) {
                PostureEnum.REFT -> Pair(PostureEnum.TAC, 0)
                PostureEnum.BAR -> Pair(PostureEnum.TAC, 0)
                PostureEnum.SHOOTSCOOT -> Pair(PostureEnum.TAC, 0)
                PostureEnum.DEPL -> Pair(PostureEnum.MOV, 0)
                PostureEnum.FARP -> Pair(PostureEnum.TAC, 1)
                else -> Pair(currentPosture, 0)
            }
        }

        fun getResult(posture: PostureEnum, unitType: UnitTypeEnum, dieRoll: DiceRollResult, modifier : Int): DisengagemenResult {

            val column = disengagementTable[posture]
                ?: throw Exception("Couldn't get disengagement column for posture $posture")
            val cell = column.get(unitType)!!

            val s0Lower = cell.s0lowerLimit
            val f1Upper = cell.f1upperLimit

            val totalValue = dieRoll.get() + modifier

            return if (s0Lower <= totalValue) {
                DisengagemenResult.S0
            } else if (f1Upper >= totalValue) {
                DisengagemenResult.F1
            } else {
                DisengagemenResult.S1
            }

        }

        private fun getDisengagementTable(): HashMap<PostureEnum,HashMap<UnitTypeEnum,DisengagementResultRangeCell>> {
            // First ROW of cells
            val INF_ADEF_CELL = DisengagementResultRangeCell(4, 7)
            val INF_MOV_CELL = DisengagementResultRangeCell(4, 8)
            val INF_DEF_CELL = DisengagementResultRangeCell(5, 10)
            val INF_OTHER_CELL = DisengagementResultRangeCell(6, 11)

            // Second ROW of cells
            val ARM_ADEF_CELL = DisengagementResultRangeCell(2, 5)
            val ARM_MOV_CELL = DisengagementResultRangeCell(2, 6)
            val ARM_DEF_CELL = DisengagementResultRangeCell(3, 8)
            val ARM_OTHER_CELL = DisengagementResultRangeCell(4, 9)

            // Third ROW of cells
            val RECON_ADEF_CELL = DisengagementResultRangeCell(1, 4)
            val RECON_MOV_CELL = DisengagementResultRangeCell(1, 5)
            val RECON_DEF_CELL = DisengagementResultRangeCell(2, 7)
            val RECON_OTHER_CELL = DisengagementResultRangeCell(3, 8)

            // ADEF_REC_SCR column
            val ADEF_REC_SCR_column : HashMap<UnitTypeEnum,DisengagementResultRangeCell> = HashMap<UnitTypeEnum,DisengagementResultRangeCell>()

            ADEF_REC_SCR_column[UnitTypeEnum.INFANTRY] = INF_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.MOTORIZED] = INF_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.TOWED_ARTILLERY] = INF_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.HELICOPTER] = INF_ADEF_CELL

            ADEF_REC_SCR_column[UnitTypeEnum.ARMOR] = ARM_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.MECHANIZED] = ARM_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.OTHER_ARTILLERY] = ARM_ADEF_CELL
            ADEF_REC_SCR_column[UnitTypeEnum.HQ] = ARM_ADEF_CELL

            ADEF_REC_SCR_column[UnitTypeEnum.RECON] = RECON_ADEF_CELL

            // MOV_TAC column
            val MOV_TAC_column : HashMap<UnitTypeEnum,DisengagementResultRangeCell> = HashMap<UnitTypeEnum,DisengagementResultRangeCell>()

            MOV_TAC_column[UnitTypeEnum.INFANTRY] = INF_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.MOTORIZED] = INF_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.TOWED_ARTILLERY] = INF_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.HELICOPTER] = INF_MOV_CELL

            MOV_TAC_column[UnitTypeEnum.ARMOR] = ARM_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.MECHANIZED] = ARM_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.OTHER_ARTILLERY] = ARM_MOV_CELL
            MOV_TAC_column[UnitTypeEnum.HQ] = ARM_MOV_CELL

            MOV_TAC_column[UnitTypeEnum.RECON] = RECON_MOV_CELL

            // DEF_MASL column
            val DEF_MASL_column : HashMap<UnitTypeEnum,DisengagementResultRangeCell> = HashMap<UnitTypeEnum,DisengagementResultRangeCell>()
            DEF_MASL_column[UnitTypeEnum.INFANTRY] = INF_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.MOTORIZED] = INF_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.TOWED_ARTILLERY] = INF_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.HELICOPTER] = INF_DEF_CELL

            DEF_MASL_column[UnitTypeEnum.ARMOR] = ARM_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.MECHANIZED] = ARM_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.OTHER_ARTILLERY] = ARM_DEF_CELL
            DEF_MASL_column[UnitTypeEnum.HQ] = ARM_DEF_CELL

            DEF_MASL_column[UnitTypeEnum.RECON] = RECON_DEF_CELL

            // OTHER column
            val OTHER_column : HashMap<UnitTypeEnum,DisengagementResultRangeCell> = HashMap<UnitTypeEnum,DisengagementResultRangeCell>()
            OTHER_column[UnitTypeEnum.INFANTRY] = INF_OTHER_CELL
            OTHER_column[UnitTypeEnum.MOTORIZED] = INF_OTHER_CELL
            OTHER_column[UnitTypeEnum.TOWED_ARTILLERY] = INF_OTHER_CELL
            OTHER_column[UnitTypeEnum.HELICOPTER] = INF_OTHER_CELL

            OTHER_column[UnitTypeEnum.ARMOR] = ARM_OTHER_CELL
            OTHER_column[UnitTypeEnum.MECHANIZED] = ARM_OTHER_CELL
            OTHER_column[UnitTypeEnum.OTHER_ARTILLERY] = ARM_OTHER_CELL
            OTHER_column[UnitTypeEnum.HQ] = ARM_OTHER_CELL

            OTHER_column[UnitTypeEnum.RECON] = RECON_OTHER_CELL

            val disengagementTable:HashMap<PostureEnum,HashMap<UnitTypeEnum,DisengagementResultRangeCell>> = HashMap<PostureEnum,HashMap<UnitTypeEnum,DisengagementResultRangeCell>>()

            disengagementTable[PostureEnum.ADEF] = ADEF_REC_SCR_column
            disengagementTable[PostureEnum.REC] = ADEF_REC_SCR_column
            disengagementTable[PostureEnum.SCRN] = ADEF_REC_SCR_column

            disengagementTable[PostureEnum.MOV] = MOV_TAC_column
            disengagementTable[PostureEnum.TAC] = MOV_TAC_column

            disengagementTable[PostureEnum.DEF] = DEF_MASL_column
            disengagementTable[PostureEnum.MASL] = DEF_MASL_column

            disengagementTable[PostureEnum.ASL] = OTHER_column
            disengagementTable[PostureEnum.FASL] = OTHER_column
            disengagementTable[PostureEnum.RDEF] = OTHER_column
            disengagementTable[PostureEnum.REFT] = OTHER_column
            disengagementTable[PostureEnum.ROAD] = OTHER_column
            disengagementTable[PostureEnum.SHOOTSCOOT] = OTHER_column
            disengagementTable[PostureEnum.BAR] = OTHER_column
            disengagementTable[PostureEnum.CSUP] = OTHER_column
            disengagementTable[PostureEnum.FARP] = OTHER_column
            disengagementTable[PostureEnum.DEPL] = OTHER_column
            disengagementTable[PostureEnum.MOV] = OTHER_column

            return disengagementTable
        }
    }

    class TerrainCombatTableRow() {
        private val contents : HashMap<MovementModeEnum, Int> = HashMap()

        fun setRow(columnCombatModifier : Int, tacticalCombatModifier : Int, deployedCombatModifier : Int) {
            contents[MovementModeEnum.COLUM] = columnCombatModifier
            contents[MovementModeEnum.TACTICAL] = tacticalCombatModifier
            contents[MovementModeEnum.DEPLOYED] = deployedCombatModifier
        }

        fun getModifier(movementModeEnum: MovementModeEnum): Int {
            val movementMode = if (movementModeEnum == MovementModeEnum.FIXED) {
                MovementModeEnum.DEPLOYED
            } else {
                movementModeEnum
            }

            return contents[movementMode]
                ?: throw Exception("Couldn't find modifier for $movementModeEnum")
        }

    }

    class TerrainCombatTable() {
        private val terrainContents : HashMap<TerrainEnum, TerrainCombatTableRow> = populateTerrainTable()
        private val obstacleContents : HashMap<ObstacleEnum, TerrainCombatTableRow> = populateObstacleTable()

        fun getCombatModifier(hexTerrain: HexTerrain, riverCrossingTypeEnum: RiverCrossingTypeEnum, defenderPostureEnums: List<PostureEnum>): Pair<Int, String> {
            val features = hexTerrain.features

            val terrainEnums : MutableList<TerrainEnum> = mutableListOf()
            for (feature in features) {
                if (feature.value) {
                    terrainEnums.add(feature.key)
                }
            }

            val terrainCombatModifier = getTerrainModifier(terrainEnums, defenderPostureEnums)
            val obstacleCombatModifier = getObstacleModifier(hexTerrain, defenderPostureEnums, riverCrossingTypeEnum)

            val explanation = terrainCombatModifier.second + obstacleCombatModifier.second
            val totalModifier = terrainCombatModifier.first + obstacleCombatModifier.first
            return Pair(totalModifier, explanation)
        }

        private fun getObstacleModifier(
            hexTerrain: HexTerrain,
            defenderPostureEnums: List<PostureEnum>,
            riverCrossingTypeEnum: RiverCrossingTypeEnum
        ): Pair<Int, String> {

            val currentObstacle = hexTerrain.getObstacle(riverCrossingTypeEnum)
                ?: return Pair(0, "No obstacles\n")

            if (riverCrossingTypeEnum == RiverCrossingTypeEnum.NONE && (currentObstacle != ObstacleEnum.MINOR_BRIDGED && currentObstacle != ObstacleEnum.MAJOR_BRIDGED)) {
                throw Exception("There is a river, but river crossing type is NONE")
            }

            val weakestMovementMode = MovementMode().getWeakestMovementMode(defenderPostureEnums)

            val obstacleRow = obstacleContents[currentObstacle]
                ?: throw Exception("No obstacle found for $currentObstacle")

            val modifier = obstacleRow.getModifier(weakestMovementMode)
            val explanation = "Obstacle: $currentObstacle, modifier for $weakestMovementMode: $modifier\n"

            return Pair(modifier, explanation)

        }

        private fun getTerrainModifier(
            terrainEnums: List<TerrainEnum>,
            defenderPostureEnums: List<PostureEnum>
        ): Pair<Int, String> {
            val weakestMovementMode = MovementMode().getWeakestMovementMode(defenderPostureEnums)

            val terrainFeatures: List<TerrainEnum> =
                listOf(TerrainEnum.SWAMP, TerrainEnum.PLAIN, TerrainEnum.TOWN, TerrainEnum.CITY)


            val activeTerrainFeatures = terrainEnums.intersect(terrainFeatures)
            val bestTerrainForDefense = findBestTerrainForDefense(activeTerrainFeatures, weakestMovementMode)

            val modifier = terrainContents[bestTerrainForDefense]!!.getModifier(weakestMovementMode)
            val explanation = "Best terrain for defense: $bestTerrainForDefense using $weakestMovementMode. Modifier: $modifier\n"

            return Pair(modifier, explanation)
        }

        private fun findBestTerrainForDefense(enums : Set<TerrainEnum>, movementModeEnum: MovementModeEnum): TerrainEnum {

            var minimumCombatModifier : Int? = null
            var bestTerrain : TerrainEnum? = null
            for (enum in enums) {
                val row = terrainContents[enum] ?: throw Exception("Couldn't find row for enum $enum")
                val combatModifier = row.getModifier(movementModeEnum)
                if (minimumCombatModifier == null) {
                    minimumCombatModifier = combatModifier
                    bestTerrain = enum
                    continue
                }

                if (minimumCombatModifier > combatModifier) {
                    minimumCombatModifier = combatModifier
                    bestTerrain = enum
                    continue
                }
            }

            if (bestTerrain == null) {
                throw Exception("bestTerrain is null for some reason")
            }

            return bestTerrain
        }

         private fun populateTerrainTable(): HashMap<TerrainEnum, TerrainCombatTableRow> {

             val contents: HashMap<TerrainEnum, TerrainCombatTableRow> = HashMap()

             val forest = TerrainCombatTableRow()
             forest.setRow(0, -1, -2)

             val town = TerrainCombatTableRow()
             town.setRow(-1, -2, -3)

             val city = TerrainCombatTableRow()
             city.setRow(-1, -3, -4)

             val plain = TerrainCombatTableRow()
             plain.setRow(0, 0, 0)

             val swamp = TerrainCombatTableRow()
             swamp.setRow(0, 0, 0)

             contents[TerrainEnum.FOREST] = forest
             contents[TerrainEnum.TOWN] = town
             contents[TerrainEnum.CITY] = city
             contents[TerrainEnum.PLAIN] = plain
             contents[TerrainEnum.SWAMP] = swamp

             return contents

         }

        private fun populateObstacleTable() : HashMap<ObstacleEnum, TerrainCombatTableRow> {
            val contents : HashMap<ObstacleEnum, TerrainCombatTableRow> = HashMap()

            val minorRiverHasty = TerrainCombatTableRow()
            minorRiverHasty.setRow(-1, -3, -3)

            val minorRiverPrepared = TerrainCombatTableRow()
            minorRiverPrepared.setRow(-1, -2, -2)

            val minorRiverBridged = TerrainCombatTableRow()
            minorRiverBridged.setRow(0, -1, -1)

            val majorRiverPrepared = TerrainCombatTableRow()
            majorRiverPrepared.setRow(-2, -4, -4)

            val majorRiverBridged = TerrainCombatTableRow()
            majorRiverBridged.setRow(-1, -3, -3)

            contents[ObstacleEnum.MINOR_HASTY] = minorRiverHasty
            contents[ObstacleEnum.MINOR_PREPARED] = minorRiverPrepared
            contents[ObstacleEnum.MINOR_BRIDGED] = minorRiverBridged

            contents[ObstacleEnum.MAJOR_PREPARED] = majorRiverPrepared
            contents[ObstacleEnum.MAJOR_BRIDGED] = majorRiverBridged

            return contents

        }

    class MovementMode() {
        private val data : HashMap<PostureEnum, MovementModeEnum> = populateData()

        fun get(posture: PostureEnum): MovementModeEnum {
            return data[posture]
                ?: throw Exception("Couldn't find movement mode for posture $posture")
        }

        fun getWeakestMovementMode(defenderPostureEnums : List<PostureEnum>) : MovementModeEnum {
            var weakestMovementMode: MovementModeEnum? = null

            for (posture in defenderPostureEnums) {
                val currentMovementMode = this.get(posture)

                if (currentMovementMode == MovementModeEnum.COLUM) {
                    weakestMovementMode = currentMovementMode
                    break
                }

                if (weakestMovementMode == null) {
                    weakestMovementMode = currentMovementMode
                    continue
                }

                if (currentMovementMode == MovementModeEnum.TACTICAL && weakestMovementMode == MovementModeEnum.DEPLOYED) {
                    weakestMovementMode = MovementModeEnum.TACTICAL
                    continue
                }

            }

            if (weakestMovementMode == null) {
                throw Exception("Weakest movement mode is null for some reason")
            }

            return weakestMovementMode
        }

        private fun populateData() : HashMap<PostureEnum, MovementModeEnum> {
            val data : HashMap<PostureEnum, MovementModeEnum> = HashMap()

            data[PostureEnum.SCRN] = MovementModeEnum.TACTICAL
            data[PostureEnum.TAC] = MovementModeEnum.TACTICAL
            data[PostureEnum.REC] = MovementModeEnum.TACTICAL
            data[PostureEnum.MASL] = MovementModeEnum.TACTICAL
            data[PostureEnum.CSUP] = MovementModeEnum.TACTICAL

            data[PostureEnum.ROAD] = MovementModeEnum.COLUM
            data[PostureEnum.MOV] = MovementModeEnum.COLUM

            data[PostureEnum.DEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.ADEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.RDEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.ASL] = MovementModeEnum.DEPLOYED
            data[PostureEnum.FASL] = MovementModeEnum.DEPLOYED
            data[PostureEnum.SHOOTSCOOT] = MovementModeEnum.DEPLOYED

            data[PostureEnum.BAR] = MovementModeEnum.FIXED
            data[PostureEnum.REFT] = MovementModeEnum.FIXED
            data[PostureEnum.FARP] = MovementModeEnum.FIXED
            data[PostureEnum.DEPL] = MovementModeEnum.FIXED

            return data
        }
    }


    }

    class AAFire() {

        private val map : HashMap<Int, Row> = populateMap()

        class Result(private val dieRoll : DiceRollResult, private val abortedAirPoints : Int, private val shotDownAirPoints : Int, private val attritionToHelicopters : Int) {
            fun getAbortedAirPoints() : Int {
                return abortedAirPoints
            }

            fun getShotDownAirPoints() : Int {
                return shotDownAirPoints
            }

            fun getAttritionToHelicopters() : Int {
                return attritionToHelicopters
            }

            fun getDieRoll() : DiceRollResult {
                return dieRoll
            }
        }

        fun getResult(die : DiceRollResult, aaFireValue : Int, againstBombardingRotary : Boolean): Result {
            var rowIndex = die.get()-1

            // 22.2.5. AA fire against bombarding rotary
            if (againstBombardingRotary) {
                rowIndex += 2
            }

            val row = map[rowIndex]!!
            val cell = row.getResult(aaFireValue) ?: return Result(die, 0,0,0)
            return Result(dieRoll = die, abortedAirPoints = cell.A, shotDownAirPoints = cell.S, attritionToHelicopters = cell.S + cell.A)
        }

        private fun populateMap() : HashMap<Int, Row> {
            val map = HashMap<Int, Row>()
            map[0] = Row(listOf(null, null, null, null, null))
            map[1] = Row(listOf(null, null, null, null, Cell(A=1, S=0)))
            map[2] = Row(listOf(null, null, null, Cell(A=1, S=0), Cell(A=1, S=0)))
            map[3] = Row(listOf(null, null, Cell(A=1, S=0), Cell(A=1, S=0), Cell(A=1, S=0)))
            map[4] = Row(listOf(null, null, Cell(A=1, S=0), Cell(A=1, S=0), Cell(A=2, S=0)))
            map[5] = Row(listOf(null, Cell(A=1, S=0), Cell(A=1, S=0), Cell(A=2, S=0), Cell(A=2, S=0)))
            map[6] = Row(listOf(null, Cell(A=1, S=0), Cell(A=2, S=0), Cell(A=2, S=0), Cell(A=1, S=1)))
            map[7] = Row(listOf(Cell(A=1, S=0), Cell(A=1, S=0), Cell(A=2, S=0), Cell(A=1, S=1), Cell(A=1, S=1)))
            map[8] = Row(listOf(Cell(A=1, S=0), Cell(A=2, S=0), Cell(A=1, S=1), Cell(A=1, S=1), Cell(A=1, S=1)))
            map[9] = Row(listOf(Cell(A=1, S=0), Cell(A=2, S=0), Cell(A=1, S=1), Cell(A=1, S=1), Cell(A=2, S=1)))
            map[10] = Row(listOf(Cell(A=2, S=0), Cell(A=1, S=1), Cell(A=1, S=1), Cell(A=2, S=1), Cell(A=2, S=1)))
            map[11] = Row(listOf(Cell(A=2, S=0), Cell(A=1, S=1), Cell(A=2, S=1), Cell(A=2, S=1), Cell(A=2, S=1)))

            return map
        }

        private class Cell(val S : Int, val A : Int) {

        }

        private class Row(cells : List<Cell?>) {
            private val map : HashMap<Int, Cell?> = populateMap(cells)

            fun getResult(AAValue : Int): Cell? {
                if (AAValue < 0) {
                    throw Exception("AA value should never be less than 0")
                }

                return when (AAValue) {
                    0 -> null
                    1,2 -> map[0]
                    3,4 -> map[1]
                    5,6 -> map[2]
                    7,8 -> map[3]
                    else -> map[4]

                }
            }

            private fun populateMap(cells : List<Cell?>) : HashMap<Int, Cell?> {
                if (cells.size != 5) {
                    throw Exception("Wrong size contents provided in populateMap for AAFire row")
                }

                val map = HashMap<Int, Cell?>()
                var idx = 0
                for (cell in cells) {
                    map[idx] = cell
                    idx += 1
                }

                return map

            }
        }
    }

    class EWResult(val combatModifier : Pair<Int, Int>, val effects : List<EwEffectEnum>) {
        fun getResultAsText(isAttacker: Boolean) : String {
            var text = if (isAttacker) {
                "Change in combat modifier: ${combatModifier.first} "
            } else {
                "Change in combat modifier: ${combatModifier.second}. "
            }

            for (effect in effects) {
                text += when (effect) {
                    EwEffectEnum.ENEMY_ARTILLERY_HALVED -> {
                        "Enemy artillery combat support halved. "
                    }
                    EwEffectEnum.ENEMY_AVIATION_HALVED -> {
                        "Enemy aviation support halved. "
                    }
                    EwEffectEnum.ENEMY_COMMAND_DISRUPTED -> {
                        "Enemy out of command and disabled front line command. "
                    }
                }
            }

            return text
        }
    }

    class EW() {

        private val map = populateTable()

        fun getResultForModifiedRoll(modifiedRoll: Int, ewPointsInUse: Int): EWResult {
            val emptyResult = EWResult(Pair(0, 0), listOf())

            if (modifiedRoll < -1) {
                return emptyResult
            }

            if (ewPointsInUse < 1) {
                return emptyResult
            }

            if (ewPointsInUse > 6) {
                throw Exception("Can't use more than 6 EW points")
            }

            if (modifiedRoll > 11) {
                throw Exception("Modified die roll can't be greater than 11")
            }

            return map[Pair(ewPointsInUse, modifiedRoll)] ?: return emptyResult
        }

        private fun populateTable(): MutableMap<Pair<Int, Int>, EWResult?> {

            // Pair(x,y) -> first column (EW points), then row (modified die result)

            val resultMap = mutableMapOf<Pair<Int, Int>, EWResult?>()

            // Column 1
            resultMap[Pair(1, -1)] = null
            resultMap[Pair(1, -0)] = null
            resultMap[Pair(1, 1)] = null
            resultMap[Pair(1, 2)] = null
            resultMap[Pair(1, 3)] = null
            resultMap[Pair(1, 4)] = null
            resultMap[Pair(1, 5)] = null
            resultMap[Pair(1, 6)] = null
            resultMap[Pair(1, 7)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(1, 8)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(1, 9)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(1, 10)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(1, 11)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))

            // Column 2
            resultMap[Pair(2, -1)] = null
            resultMap[Pair(2, -0)] = null
            resultMap[Pair(2, 1)] = null
            resultMap[Pair(2, 2)] = null
            resultMap[Pair(2, 3)] = null
            resultMap[Pair(2, 4)] = null
            resultMap[Pair(2, 5)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(2, 6)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(2, 7)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(2, 8)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(2, 9)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(2, 10)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(2, 11)] = EWResult(Pair(2, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))

            // Column 3
            resultMap[Pair(3, -1)] = null
            resultMap[Pair(3, -0)] = null
            resultMap[Pair(3, 1)] = null
            resultMap[Pair(3, 2)] = null
            resultMap[Pair(3, 3)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(3, 4)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(3, 5)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(3, 6)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(3, 7)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(3, 8)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(3, 9)] = EWResult(Pair(2, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(3, 10)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(3, 11)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))

            // Column 4
            resultMap[Pair(4, -1)] = null
            resultMap[Pair(4, -0)] = null
            resultMap[Pair(4, 1)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(4, 2)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(4, 3)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(4, 4)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(4, 5)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(4, 6)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(4, 7)] = EWResult(Pair(2, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(4, 8)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(4, 9)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(4, 10)] = EWResult(Pair(3, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(4, 11)] = EWResult(Pair(4, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))

            // Column 5
            resultMap[Pair(5, -1)] = null
            resultMap[Pair(5, -0)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(5, 1)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(5, 2)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(5, 3)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(5, 4)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(5, 5)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(5, 6)] = EWResult(Pair(2, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(5, 7)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(5, 8)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(5, 9)] = EWResult(Pair(3, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(5, 10)] = EWResult(Pair(4, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(5, 11)] = EWResult(Pair(4, -6), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))

            // Column 6
            resultMap[Pair(6, -1)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(6, -0)] = EWResult(Pair(1, -2), listOf())
            resultMap[Pair(6, 1)] = EWResult(Pair(1, -2), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(6, 2)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(6, 3)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED))
            resultMap[Pair(6, 4)] = EWResult(Pair(2, -3), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(6, 5)] = EWResult(Pair(2, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(6, 6)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED))
            resultMap[Pair(6, 7)] = EWResult(Pair(3, -4), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(6, 8)] = EWResult(Pair(3, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(6, 9)] = EWResult(Pair(4, -5), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(6, 10)] = EWResult(Pair(4, -6), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))
            resultMap[Pair(6, 11)] = EWResult(Pair(4, -6), listOf(EwEffectEnum.ENEMY_AVIATION_HALVED, EwEffectEnum.ENEMY_ARTILLERY_HALVED, EwEffectEnum.ENEMY_COMMAND_DISRUPTED))

            return resultMap
        }

    }

    class CombatSupport() {

        private val attackerTable = initAttackerTable()
        private val defenderTable = initDefenderTable()

        fun calculateForAttacker(combatSupportPoints : Int, dieRoll: DiceRollResult) : Int {
            return calculate(combatSupportPoints, dieRoll, true)
        }

        fun calculateForDefender(combatSupportPoints : Int, dieRoll: DiceRollResult) : Int {
            return calculate(combatSupportPoints, dieRoll, false)
        }

        private fun calculate(combatSupportPoints : Int, dieRoll: DiceRollResult, isAttacker: Boolean) : Int {
            if (combatSupportPoints < 0) {
                // TODO there has been a case where this happens. Needs investigation. Probably related to helicopter attrition
                throw Exception("Combat support points can't be smaller than 0")
            }

            val validatedPoints = if (combatSupportPoints > 16) {
                16
            } else {
                combatSupportPoints
            }

            val row = if (isAttacker) {
                // Die roll from 1 to 10, table from 0 to 9
                attackerTable[dieRoll.get()-1]
            } else {
                defenderTable[dieRoll.get()-1]
            }

            return when (validatedPoints) {
                0 -> row[0]
                1,2-> row[1]
                3,4-> row[2]
                5,6-> row[3]
                7,8-> row[4]
                9,10-> row[5]
                11,12-> row[6]
                13,14,15-> row[7]
                16-> row[8]
                else -> throw Exception("Couldn't map validatedPoints $validatedPoints")
            }
        }

        private fun initAttackerTable() : MutableList<List<Int>> {
            // each list represents a row
            val list = mutableListOf<List<Int>>()

            list.add(listOf(-3, -2, -2, -1, 0, 1, 2, 2, 2))
            list.add(listOf(-2, -2, -1, 0, 0, 2, 2, 2, 3))
            list.add(listOf(-2, -2, -1, 0, 1, 2, 2, 3, 3))
            list.add(listOf(-2, -1, 0, 0, 2, 2, 3, 3, 4))
            list.add(listOf(-1, -1, 0, 1, 2, 3, 3, 4, 4))
            list.add(listOf(-1, -1, 0, 1, 2, 3, 4, 4, 5))
            list.add(listOf(-1, 0, 0, 2, 3, 4, 4, 5, 5))
            list.add(listOf(0, 0, 1, 2, 3, 4, 5, 5, 6))
            list.add(listOf(0, 1, 1, 2, 4, 5, 5, 6, 6))
            list.add(listOf(0, 1, 2, 3, 4, 5, 6, 6, 7))

            return list
        }

        private fun initDefenderTable() : MutableList<List<Int>> {
            // each list represents a row
            val list = mutableListOf<List<Int>>()

            list.add(listOf(0, 0, 0, 0, -1, -2, -2, -3, -3))
            list.add(listOf(0, 0, 0, -1, -1, -2, -3, -3, -4))
            list.add(listOf(0, 0, 0, -1, -2, -2, -3, -4, -4))
            list.add(listOf(0, 0, -1, -1, -2, -3, -3, -4, -4))
            list.add(listOf(0, 0, -1, -2, -2, -3, -4, -4, -5))
            list.add(listOf(0, -1, -1, -2, -3, -3, -4, -5, -5))
            list.add(listOf(0, -1, -2, -2, -3, -4, -4, -5, -5))
            list.add(listOf(0, -1, -2, -3, -3, -4, -5, -5, -6))
            list.add(listOf(0, -1, -1, -2, -4, -5, -5, -6, -6))
            list.add(listOf(0, -1, -2, -3, -4, -5, -6, -6, -7))

            return list
        }
    }

    class GroundCombatResult(val attackerAttrition : Int, val attackerSapperEliminated: Boolean, val defenderAttrition : Int, val defenderSapperEliminated: Boolean) {}

    class GroundCombat() {
        // table needs to be searched Pair<y,x>
        private val table = initTable()

        fun getResult(dieRoll : DiceRollResult, combatDifferential : Int, attackerInFullAssault : Boolean) : GroundCombatResult {
            val finalDiff = if (combatDifferential > 9) {
                9
            } else if (combatDifferential < -5) {
                -5
            } else {
                combatDifferential
            }

            val result = table[Pair(dieRoll.get(), finalDiff)]
                ?: throw Exception("Couldn't find result for ${dieRoll.get()} and $finalDiff")

            // 6.3.3 Full Assault Posture
            if (result.attackerAttrition > 0 && attackerInFullAssault) {
                return GroundCombatResult(
                    attackerAttrition = result.attackerAttrition + 1,
                    defenderAttrition = result.defenderAttrition,
                    attackerSapperEliminated = result.attackerSapperEliminated,
                    defenderSapperEliminated = result.defenderSapperEliminated
                )
            }

            return result
        }

        private fun initTable() : Map<Pair<Int, Int>, GroundCombatResult> {
            // x = combat modifier, y = die roll result
            val table = mutableMapOf<Pair<Int, Int>, GroundCombatResult>()

            table[Pair(1, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(3, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(4, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(5, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(6, -5)] = GroundCombatResult(2, true, 0, false)
            table[Pair(7, -5)] = GroundCombatResult(1, true, 0, false)
            table[Pair(8, -5)] = GroundCombatResult(1, true, 0, false)
            table[Pair(9, -5)] = GroundCombatResult(1, false, 0, false)
            table[Pair(10, -5)] = GroundCombatResult(0, false, 0, true)

            table[Pair(1, -4)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, -4)] = GroundCombatResult(2, true, 0, false)
            table[Pair(3, -4)] = GroundCombatResult(2, true, 0, false)
            table[Pair(4, -4)] = GroundCombatResult(2, true, 0, false)
            table[Pair(5, -4)] = GroundCombatResult(2, true, 0, false)
            table[Pair(6, -4)] = GroundCombatResult(1, true, 0, false)
            table[Pair(7, -4)] = GroundCombatResult(1, true, 0, false)
            table[Pair(8, -4)] = GroundCombatResult(1, true, 0, false)
            table[Pair(9, -4)] = GroundCombatResult(0, false, 0, false)
            table[Pair(10, -4)] = GroundCombatResult(0, false, 0, true)

            table[Pair(1, -3)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, -3)] = GroundCombatResult(2, true, 0, false)
            table[Pair(3, -3)] = GroundCombatResult(2, true, 0, false)
            table[Pair(4, -3)] = GroundCombatResult(2, true, 0, false)
            table[Pair(5, -3)] = GroundCombatResult(1, true, 0, false)
            table[Pair(6, -3)] = GroundCombatResult(1, true, 0, false)
            table[Pair(7, -3)] = GroundCombatResult(1, true, 0, false)
            table[Pair(8, -3)] = GroundCombatResult(0, false, 0, false)
            table[Pair(9, -3)] = GroundCombatResult(0, false, 0, true)
            table[Pair(10, -3)] = GroundCombatResult(1, false, 1, true)

            table[Pair(1, -2)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, -2)] = GroundCombatResult(2, true, 0, false)
            table[Pair(3, -2)] = GroundCombatResult(2, true, 0, false)
            table[Pair(4, -2)] = GroundCombatResult(2, true, 1, false)
            table[Pair(5, -2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(6, -2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(7, -2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(8, -2)] = GroundCombatResult(0, false, 0, false)
            table[Pair(9, -2)] = GroundCombatResult(0, false, 0, true)
            table[Pair(10, -2)] = GroundCombatResult(1, false, 1, true)

            table[Pair(1, -1)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, -1)] = GroundCombatResult(2, true, 0, false)
            table[Pair(3, -1)] = GroundCombatResult(2, true, 1, false)
            table[Pair(4, -1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(5, -1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(6, -1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(7, -1)] = GroundCombatResult(0, false, 0, false)
            table[Pair(8, -1)] = GroundCombatResult(0, false, 0, true)
            table[Pair(9, -1)] = GroundCombatResult(1, false, 1, true)
            table[Pair(10, -1)] = GroundCombatResult(1, false, 1, true)

            table[Pair(1, 0)] = GroundCombatResult(2, true, 0, false)
            table[Pair(2, 0)] = GroundCombatResult(2, true, 1, false)
            table[Pair(3, 0)] = GroundCombatResult(1, true, 0, false)
            table[Pair(4, 0)] = GroundCombatResult(1, true, 0, false)
            table[Pair(5, 0)] = GroundCombatResult(1, true, 0, false)
            table[Pair(6, 0)] = GroundCombatResult(0, true, 0, false)
            table[Pair(7, 0)] = GroundCombatResult(0, false, 0, false)
            table[Pair(8, 0)] = GroundCombatResult(1, false, 1, true)
            table[Pair(9, 0)] = GroundCombatResult(1, false, 1, true)
            table[Pair(10, 0)] = GroundCombatResult(0, false, 1, true)

            table[Pair(1, 1)] = GroundCombatResult(2, true, 1, false)
            table[Pair(2, 1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(3, 1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(4, 1)] = GroundCombatResult(1, true, 0, false)
            table[Pair(5, 1)] = GroundCombatResult(0, true, 0, false)
            table[Pair(6, 1)] = GroundCombatResult(0, false, 0, false)
            table[Pair(7, 1)] = GroundCombatResult(0, false, 0, false)
            table[Pair(8, 1)] = GroundCombatResult(1, false, 1, true)
            table[Pair(9, 1)] = GroundCombatResult(1, false, 1, true)
            table[Pair(10, 1)] = GroundCombatResult(0, false, 1, true)

            table[Pair(1, 2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(2, 2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(3, 2)] = GroundCombatResult(1, true, 0, false)
            table[Pair(4, 2)] = GroundCombatResult(0, true, 0, false)
            table[Pair(5, 2)] = GroundCombatResult(0, true, 0, false)
            table[Pair(6, 2)] = GroundCombatResult(0, false, 0, false)
            table[Pair(7, 2)] = GroundCombatResult(1, false, 1, true)
            table[Pair(8, 2)] = GroundCombatResult(1, false, 1, true)
            table[Pair(9, 2)] = GroundCombatResult(0, false, 1, true)
            table[Pair(10, 2)] = GroundCombatResult(0, false, 1, true)

            table[Pair(1, 3)] = GroundCombatResult(1, true, 0, false)
            table[Pair(2, 3)] = GroundCombatResult(1, true, 0, false)
            table[Pair(3, 3)] = GroundCombatResult(0, true, 0, false)
            table[Pair(4, 3)] = GroundCombatResult(0, true, 0, false)
            table[Pair(5, 3)] = GroundCombatResult(0, false, 0, false)
            table[Pair(6, 3)] = GroundCombatResult(1, false, 1, true)
            table[Pair(7, 3)] = GroundCombatResult(1, false, 1, true)
            table[Pair(8, 3)] = GroundCombatResult(0, false, 1, true)
            table[Pair(9, 3)] = GroundCombatResult(0, false, 1, true)
            table[Pair(10, 3)] = GroundCombatResult(0, false, 1, true)

            table[Pair(1, 4)] = GroundCombatResult(1, true, 0, false)
            table[Pair(2, 4)] = GroundCombatResult(0, true, 0, false)
            table[Pair(3, 4)] = GroundCombatResult(0, true, 0, false)
            table[Pair(4, 4)] = GroundCombatResult(1, true, 1, false)
            table[Pair(5, 4)] = GroundCombatResult(1, false, 1, false)
            table[Pair(6, 4)] = GroundCombatResult(0, false, 1, true)
            table[Pair(7, 4)] = GroundCombatResult(0, false, 1, true)
            table[Pair(8, 4)] = GroundCombatResult(0, false, 1, true)
            table[Pair(9, 4)] = GroundCombatResult(1, false, 2, true)
            table[Pair(10, 4)] = GroundCombatResult(1, false, 2, true)

            table[Pair(1, 5)] = GroundCombatResult(0, true, 0, false)
            table[Pair(2, 5)] = GroundCombatResult(1, true, 1, false)
            table[Pair(3, 5)] = GroundCombatResult(1, true, 1, false)
            table[Pair(4, 5)] = GroundCombatResult(1, false, 1, false)
            table[Pair(5, 5)] = GroundCombatResult(0, false, 1, true)
            table[Pair(6, 5)] = GroundCombatResult(0, false, 1, true)
            table[Pair(7, 5)] = GroundCombatResult(0, false, 1, true)
            table[Pair(8, 5)] = GroundCombatResult(1, false, 2, true)
            table[Pair(9, 5)] = GroundCombatResult(1, false, 2, true)
            table[Pair(10, 5)] = GroundCombatResult(0, false, 2, true)

            table[Pair(1, 6)] = GroundCombatResult(1, true, 1, false)
            table[Pair(2, 6)] = GroundCombatResult(1, true, 1, false)
            table[Pair(3, 6)] = GroundCombatResult(1, true, 1, false)
            table[Pair(4, 6)] = GroundCombatResult(0, false, 1, false)
            table[Pair(5, 6)] = GroundCombatResult(0, false, 1, true)
            table[Pair(6, 6)] = GroundCombatResult(0, false, 1, true)
            table[Pair(7, 6)] = GroundCombatResult(1, false, 2, true)
            table[Pair(8, 6)] = GroundCombatResult(1, false, 2, true)
            table[Pair(9, 6)] = GroundCombatResult(0, false, 2, true)
            table[Pair(10, 6)] = GroundCombatResult(0, false, 2, true)

            table[Pair(1, 7)] = GroundCombatResult(1, true, 1, false)
            table[Pair(2, 7)] = GroundCombatResult(1, true, 1, false)
            table[Pair(3, 7)] = GroundCombatResult(0, false, 1, false)
            table[Pair(4, 7)] = GroundCombatResult(0, false, 1, true)
            table[Pair(5, 7)] = GroundCombatResult(0, false, 1, true)
            table[Pair(6, 7)] = GroundCombatResult(1, false, 2, true)
            table[Pair(7, 7)] = GroundCombatResult(1, false, 2, true)
            table[Pair(8, 7)] = GroundCombatResult(0, false, 2, true)
            table[Pair(9, 7)] = GroundCombatResult(0, false, 2, true)
            table[Pair(10, 7)] = GroundCombatResult(0, false, 2, true)

            table[Pair(1, 8)] = GroundCombatResult(1, true, 1, false)
            table[Pair(2, 8)] = GroundCombatResult(0, true, 1, false)
            table[Pair(3, 8)] = GroundCombatResult(0, false, 1, false)
            table[Pair(4, 8)] = GroundCombatResult(0, false, 1, true)
            table[Pair(5, 8)] = GroundCombatResult(1, false, 2, true)
            table[Pair(6, 8)] = GroundCombatResult(1, false, 2, true)
            table[Pair(7, 8)] = GroundCombatResult(0, false, 2, true)
            table[Pair(8, 8)] = GroundCombatResult(0, false, 2, true)
            table[Pair(9, 8)] = GroundCombatResult(0, false, 2, true)
            table[Pair(10, 8)] = GroundCombatResult(0, false, 2, true)

            table[Pair(1, 9)] = GroundCombatResult(0, true, 1, false)
            table[Pair(2, 9)] = GroundCombatResult(0, true, 1, false)
            table[Pair(3, 9)] = GroundCombatResult(0, false, 1, false)
            table[Pair(4, 9)] = GroundCombatResult(1, false, 2, true)
            table[Pair(5, 9)] = GroundCombatResult(1, false, 2, true)
            table[Pair(6, 9)] = GroundCombatResult(0, false, 2, true)
            table[Pair(7, 9)] = GroundCombatResult(0, false, 2, true)
            table[Pair(8, 9)] = GroundCombatResult(0, false, 2, true)
            table[Pair(9, 9)] = GroundCombatResult(0, false, 2, true)
            table[Pair(10, 9)] = GroundCombatResult(0, false, 2, true)

            return table
        }
    }

    class BombardmentResult(combatUnitAttrition : Int, supportUnitAttrition : Int, targetHalfEngaged : Boolean) {
        val combatUnitAttrition = combatUnitAttrition
        val supportUnitAttrition = supportUnitAttrition
        val targetHalfEngaged = targetHalfEngaged
    }

    class BombardmentTable() {
        private val table = initTable()

        fun getResult(
            detectionLevel: Int,
            totalDetectionModifiers: Int,
            bombardmentValue: Int,
            dieRoll: DiceRollResult
        ): BombardmentResult {
            val rowIndex = getBombardmentRowIndex(detectionLevel, dieRoll.get()+totalDetectionModifiers)
            val resultRow = table[rowIndex]
                ?: throw Exception("Couldn't get bombardment result with det modifiers $totalDetectionModifiers detection $detectionLevel, bombardment $bombardmentValue, die roll ${dieRoll.get()}, rowIndex $rowIndex")

            val columnIndex = mapBombardmentValueToColumnIndex(bombardmentValue)
            if (columnIndex < 0) {
                return BombardmentResult(0,0,false)
            }

            val result = resultRow[columnIndex]
            return result
        }

        // 0-13
        private fun getBombardmentRowIndex(detectionLevel : Int, modifiedDieRoll: Int) : Int {
            if (detectionLevel < 1 || detectionLevel > 4) {
                return -1
            }

            val modifiedRollsDetection1 = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
            val modifiedRollsDetection2 = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
            val modifiedRollsDetection3 = listOf(-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
            val modifiedRollsDetection4 = listOf(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

            val result = when (detectionLevel) {
                1 -> modifiedRollsDetection1.indexOf(modifiedDieRoll)
                2 -> modifiedRollsDetection2.indexOf(modifiedDieRoll)
                3 -> modifiedRollsDetection3.indexOf(modifiedDieRoll)
                else -> modifiedRollsDetection4.indexOf(modifiedDieRoll)
            }

            if (result == -1) {
                if (modifiedDieRoll >= 10) {
                    return modifiedRollsDetection4.lastIndex
                } else if (modifiedDieRoll <= 1) {
                    return 0
                }
                throw Exception("Couldn't find bombardment row index for modifiedDieRoll $modifiedDieRoll and detectionLevel $detectionLevel")
            }

            return result

        }

        private fun mapBombardmentValueToColumnIndex(bombardmentValue : Int) : Int {
            return when {
                (bombardmentValue <= 0) -> -1
                (bombardmentValue == 1 || bombardmentValue == 2) -> 0
                (bombardmentValue == 3 || bombardmentValue == 4) -> 1
                (bombardmentValue == 5 || bombardmentValue == 6) -> 2
                (bombardmentValue == 7 || bombardmentValue == 8) -> 3
                (bombardmentValue == 9 || bombardmentValue == 10) -> 4
                (bombardmentValue == 11 || bombardmentValue == 12) -> 5
                else -> 6
            }
        }

        private fun initTable() : Map<Int, List<BombardmentResult>> {

            val map = mutableMapOf<Int, List<BombardmentResult>>()
            val emptyResult = BombardmentResult(0,0,false)

            map[0] = listOf(
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult
            )

            map[1] = listOf(
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                BombardmentResult(0,0,true)
            )

            map[2] = listOf(
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true)
            )

            map[3] = listOf(
                emptyResult,
                emptyResult,
                emptyResult,
                emptyResult,
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true)
            )

            map[4] = listOf(
                emptyResult,
                emptyResult,
                emptyResult,
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true)
            )

            map[5] = listOf(
                emptyResult,
                emptyResult,
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true)
            )

            map[6] = listOf(
                emptyResult,
                emptyResult,
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true)
            )

            map[7] = listOf(
                emptyResult,
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true)
            )

            map[8] = listOf(
                emptyResult,
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true)
            )

            map[9] = listOf(
                BombardmentResult(0,0,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true)
            )

            map[10] = listOf(
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true)
            )

            map[11] = listOf(
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true)
            )

            map[12] = listOf(
                BombardmentResult(0,1,true),
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(2,2,true)
            )

            map[13] = listOf(
                BombardmentResult(0,1,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(1,2,true),
                BombardmentResult(2,2,true),
                BombardmentResult(2,2,true)
            )


            return map
        }
    }

    class InterdictionTable() {

        // TODO add LARS support

        private val table = initTable()

        private fun initTable() : MutableList<List<Int>> {
            val table : MutableList<List<Int>> = mutableListOf()
            table.add(listOf(0,0,2,2,2))
            table.add(listOf(0,2,2,2,2))
            table.add(listOf(0,2,2,2,4))
            table.add(listOf(0,2,2,2,4))
            table.add(listOf(0,2,2,4,4))
            table.add(listOf(2,2,2,4,4))
            table.add(listOf(2,2,4,4,4))
            table.add(listOf(2,2,4,4,4))
            table.add(listOf(2,4,4,4,4))
            table.add(listOf(2,4,4,4,4))

            return table
        }

        fun getResult(interdictionValue : Int, dieRoll: DiceRollResult) : Pair<String, Boolean> {
            val interdictionResult = when (interdictionValue) {
                in 1..2 -> table[dieRoll.get()][0]
                in 3..4 -> table[dieRoll.get()][1]
                in 5..6 -> table[dieRoll.get()][2]
                in 7..8 -> table[dieRoll.get()][3]
                in 9..10 -> table[dieRoll.get()][4]
                else -> throw Exception("Error getting interdiction result $interdictionValue, ${dieRoll.get()}")
            }

            return if (interdictionResult > 0) {
                Pair("Total strength: $interdictionValue. Dice roll: ${dieRoll.get()}\n\nInterdiction successful! Total value: $interdictionResult", true)
            } else {
                Pair("Total strength: $interdictionValue. Dice roll: ${dieRoll.get()}\n\nInterdiction failed", false)
            }
        }
    }

}