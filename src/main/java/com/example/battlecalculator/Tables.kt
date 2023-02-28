package com.example.battlecalculator



class Tables {

    private class DisengagementResultRangeCell(f1upperLimit: Int, s0lowerLimit: Int) {
        var f1upperLimit: Int = f1upperLimit
        var s0lowerLimit: Int = s0lowerLimit

    }

    private val disengagementTable = getDisengagementTable()

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

        return disengagementTable
    }

    class TerrainCombatTableRow() {
        private val contents : HashMap<MovementModeEnum, Int> = HashMap()

        fun setRow(columnCombatModifier : Int, tacticalCombatModifier : Int, deployedCombatModifier : Int) {
            contents[MovementModeEnum.COLUM] = columnCombatModifier
            contents[MovementModeEnum.TACTICAL] = tacticalCombatModifier
            contents[MovementModeEnum.DEPLOYED] = deployedCombatModifier
        }

        fun getModifier(movementModeEnum: MovementModeEnum): Int {
            return contents[movementModeEnum]
                ?: throw Exception("Couldn't find modifier for $movementModeEnum")
        }

    }

    class TerrainCombatTable() {
        private val terrainContents : HashMap<TerrainEnum, TerrainCombatTableRow> = populateTerrainTable()
        private val obstacleContents : HashMap<ObstacleEnum, TerrainCombatTableRow> = populateObstacleTable()

        private enum class ObstacleEnum {
            MINOR_HASTY, MINOR_PREPARED, MINOR_BRIDGED, MAJOR_PREPARED, MAJOR_BRIDGED
        }

        fun getCombatModifier(hexTerrain: HexTerrain, riverCrossingTypeEnum: RiverCrossingTypeEnum, defenderPostureEnums: List<PostureEnum>): Int {
            val features = hexTerrain.features

            val terrainEnums : MutableList<TerrainEnum> = mutableListOf()
            for (feature in features) {
                if (feature.value) {
                    terrainEnums.add(feature.key)
                }
            }

            val terrainCombatModifier = getTerrainModifier(terrainEnums, defenderPostureEnums)
            val obstacleCombatModifier = getObstacleModifier(terrainEnums, defenderPostureEnums, riverCrossingTypeEnum)
            return terrainCombatModifier + obstacleCombatModifier
        }

        private fun getObstacleModifier(
            terrainEnums: List<TerrainEnum>,
            defenderPostureEnums: List<PostureEnum>,
            riverCrossingTypeEnum: RiverCrossingTypeEnum
        ): Int {

            if (!terrainEnums.contains(TerrainEnum.MAJORRIVER) && !terrainEnums.contains(TerrainEnum.MINORRIVER)) {
                // No obstacles
                return 0
            }

            if (riverCrossingTypeEnum == RiverCrossingTypeEnum.NONE) {
                throw Exception("There is a river, but river crossing type is NONE")
            }

            val currentObstacle : ObstacleEnum
            if (terrainEnums.contains(TerrainEnum.BRIDGE)) {
                currentObstacle = if (terrainEnums.contains(TerrainEnum.MINORRIVER)) {
                    ObstacleEnum.MINOR_BRIDGED
                } else if(terrainEnums.contains(TerrainEnum.MAJORRIVER)) {
                    ObstacleEnum.MAJOR_PREPARED
                } else {
                    throw Exception("There is a bridge but no river")
                }
            } else {
                if (terrainEnums.contains(TerrainEnum.MINORRIVER)) {
                    currentObstacle = if (riverCrossingTypeEnum == RiverCrossingTypeEnum.HASTY) {
                        ObstacleEnum.MINOR_HASTY
                    } else if(riverCrossingTypeEnum == RiverCrossingTypeEnum.PREPARED) {
                        ObstacleEnum.MINOR_PREPARED
                    } else {
                        throw Exception("Crossing minor river with no crossing type")
                    }
                } else {
                    // Case major river
                    if (riverCrossingTypeEnum == RiverCrossingTypeEnum.PREPARED) {
                        currentObstacle = ObstacleEnum.MINOR_PREPARED
                    } else {
                        throw Exception("Attempting to cross major river without preparation")
                    }
                }
            }

            val weakestMovementMode = MovementMode().getWeakestMovementMode(defenderPostureEnums)

            val obstacleRow = obstacleContents[currentObstacle]
                ?: throw Exception("No obstacle found for $currentObstacle")

            return obstacleRow.getModifier(weakestMovementMode)

        }

        private fun getTerrainModifier(
            terrainEnums: List<TerrainEnum>,
            defenderPostureEnums: List<PostureEnum>
        ): Int {
            val weakestMovementMode = MovementMode().getWeakestMovementMode(defenderPostureEnums)

            val terrainFeatures: List<TerrainEnum> =
                listOf(TerrainEnum.SWAMP, TerrainEnum.PLAIN, TerrainEnum.TOWN, TerrainEnum.CITY)


            val activeTerrainFeatures = terrainEnums.intersect(terrainFeatures)
            val bestTerrainForDefense = findBestTerrainForDefense(activeTerrainFeatures, weakestMovementMode)

            return terrainContents[bestTerrainForDefense]!!.getModifier(weakestMovementMode)
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
                ?: throw Exception("Couldn't find movement mode for posture ${posture.toString()}")
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

            data[PostureEnum.ROAD] = MovementModeEnum.COLUM

            data[PostureEnum.DEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.ADEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.RDEF] = MovementModeEnum.DEPLOYED
            data[PostureEnum.ASL] = MovementModeEnum.DEPLOYED
            data[PostureEnum.FASL] = MovementModeEnum.DEPLOYED

            return data
        }
    }


    }

    class AAFire() {

        private val map : HashMap<Int, Row> = populateMap()

        class Result(private val abortedAirPoints : Int, private val shotDownAirPoints : Int, private val attritionToHelicopters : Int) {
            fun getAbortedAirPoints() : Int {
                return abortedAirPoints
            }

            fun getShotDownAirPoints() : Int {
                return shotDownAirPoints
            }

            fun getAttritionToHelicopters() : Int {
                return attritionToHelicopters
            }
        }

        fun getResult(die : DieRoll, aaFireValue : Int): Result {
            val row = map[die.getResultWithModifiers()-1]!!
            val cell = row.getResult(aaFireValue) ?: return Result(0,0,0)
            return Result(abortedAirPoints = cell.A, shotDownAirPoints = cell.S, attritionToHelicopters = cell.S + cell.A)
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

}