package com.example.battlecalculator

class Movement() {

    private val obstacleTable = ObstacleTable()
    private val terrainTypeTable = TerrainTypeTable()

    class TerrainTypeTable() {
        private val terrainType = initTerrainType()

        /*fun getRow(terrainEnum: TerrainEnum) {
            return terrainType[terrainEnum]
        }*/

        private fun initTerrainType() : HashMap<TerrainEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, Int>>?> {
            val table = HashMap<TerrainEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, Int>>?>()

            val plainRow = hashMapOf(
                MovementTypeEnum.MECHANIZED to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 2,
                    MovementModeEnum.DEPLOYED to 3
                ),
                MovementTypeEnum.MOTORIZED to hashMapOf(
                    MovementModeEnum.COLUM to 1,
                    MovementModeEnum.TACTICAL to 2,
                    MovementModeEnum.DEPLOYED to 3
                ),
                MovementTypeEnum.FOOT to hashMapOf(
                    MovementModeEnum.COLUM to 3,
                    MovementModeEnum.TACTICAL to 3,
                    MovementModeEnum.DEPLOYED to 3
                )
            )

            val townRow = hashMapOf(
                MovementTypeEnum.MECHANIZED to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 2,
                    MovementModeEnum.DEPLOYED to 3
                ),
                MovementTypeEnum.MOTORIZED to hashMapOf(
                    MovementModeEnum.COLUM to 1,
                    MovementModeEnum.TACTICAL to 2,
                    MovementModeEnum.DEPLOYED to 3
                ),
                MovementTypeEnum.FOOT to hashMapOf(
                    MovementModeEnum.COLUM to 3,
                    MovementModeEnum.TACTICAL to 3,
                    MovementModeEnum.DEPLOYED to 3
                )
            )

            val cityRow = hashMapOf(
                MovementTypeEnum.MECHANIZED to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 3,
                    MovementModeEnum.DEPLOYED to 4
                ),
                MovementTypeEnum.MOTORIZED to hashMapOf(
                    MovementModeEnum.COLUM to 1,
                    MovementModeEnum.TACTICAL to 3,
                    MovementModeEnum.DEPLOYED to 4
                ),
                MovementTypeEnum.FOOT to hashMapOf(
                    MovementModeEnum.COLUM to 4,
                    MovementModeEnum.TACTICAL to 4,
                    MovementModeEnum.DEPLOYED to 4
                )
            )

            table[TerrainEnum.PLAIN] = plainRow
            table[TerrainEnum.TOWN] = townRow
            table[TerrainEnum.CITY] = cityRow

            return table

        }
    }

    class TerrainFeatureTable() {
        private val terrainFeature = initTerrainFeature()

        private fun initTerrainFeature() : HashMap<TerrainEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, Int>>?> {
            val table = HashMap<TerrainEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, Int>>?>()

            val forestRow = hashMapOf(
                MovementTypeEnum.MECHANIZED to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 2,
                    MovementModeEnum.DEPLOYED to 3
                ),
                MovementTypeEnum.MOTORIZED to hashMapOf(
                    MovementModeEnum.COLUM to 4,
                    MovementModeEnum.TACTICAL to 4,
                    MovementModeEnum.DEPLOYED to 6
                ),
                MovementTypeEnum.FOOT to hashMapOf(
                    MovementModeEnum.COLUM to 1,
                    MovementModeEnum.TACTICAL to 1,
                    MovementModeEnum.DEPLOYED to 2
                )
            )

            val floodRow = hashMapOf(
                MovementTypeEnum.MECHANIZED to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 4,
                    MovementModeEnum.DEPLOYED to 6
                ),
                MovementTypeEnum.MOTORIZED to hashMapOf(
                    MovementModeEnum.COLUM to 4,
                    MovementModeEnum.TACTICAL to 6,
                    MovementModeEnum.DEPLOYED to 8
                ),
                MovementTypeEnum.FOOT to hashMapOf(
                    MovementModeEnum.COLUM to 2,
                    MovementModeEnum.TACTICAL to 3,
                    MovementModeEnum.DEPLOYED to 4
                )
            )

            table[TerrainEnum.FOREST] = forestRow
            table[TerrainEnum.SWAMP] = floodRow

            return table

        }
    }

    private fun getTerrainTypeCost(terrainEnums: List<TerrainEnum>, best: Boolean) {
        for (terrainEnum in terrainEnums) {
            //terrainTypeTable[terrainEnum]
        }
    }

    private fun getWorstTerrainType(terrainEnums: List<TerrainEnum>) {

    }

    fun getMovementPointCost(movementTypeEnum: MovementTypeEnum, movementModeEnum: MovementModeEnum, terrainEnums: List<TerrainEnum>) {

        when (movementModeEnum) {
            MovementModeEnum.COLUM -> 0
            MovementModeEnum.TACTICAL -> 0
            MovementModeEnum.DEPLOYED -> 0
            MovementModeEnum.FIXED -> throw Exception("Units in fixed movement mode can't move")
        }

    }

    class ObstacleTable() {
        // Here we init the row for hasty crossings. When the whole table is implemented, move this away from this class
        private val contents = init()

        fun getCell(obstacleEnum: ObstacleEnum, movementTypeEnum: MovementTypeEnum, movementModeEnum: MovementModeEnum) : ObstacleTableCell {
            val row = contents[obstacleEnum] ?: throw Exception("Obstacle table: $obstacleEnum not implemented")

            return row[movementTypeEnum]!![movementModeEnum]!!
        }

        private fun init() : HashMap<ObstacleEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>?> {
            val table = HashMap<ObstacleEnum, HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>?>()

            val minorHastyRow = HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>()

            // By chance the column is same for all movement modes
            val minorHastyColumn = HashMap<MovementModeEnum, ObstacleTableCell>()
            minorHastyColumn[MovementModeEnum.COLUM] = ObstacleTableCell(2, 3, false)
            minorHastyColumn[MovementModeEnum.TACTICAL] = ObstacleTableCell(2, 4, false)
            minorHastyColumn[MovementModeEnum.DEPLOYED] = ObstacleTableCell(3, 5, false)

            minorHastyRow[MovementTypeEnum.MECHANIZED] = minorHastyColumn
            minorHastyRow[MovementTypeEnum.MOTORIZED] = minorHastyColumn
            minorHastyRow[MovementTypeEnum.FOOT] = minorHastyColumn

            val minorPreparedRow = HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>()

            // By chance the column is same for all movement modes
            val minorPreparedColumn = HashMap<MovementModeEnum, ObstacleTableCell>()
            minorPreparedColumn[MovementModeEnum.COLUM] = ObstacleTableCell(4, null, false)
            minorPreparedColumn[MovementModeEnum.TACTICAL] = ObstacleTableCell(4, null, false)
            minorPreparedColumn[MovementModeEnum.DEPLOYED] = ObstacleTableCell(5, null, false)

            minorPreparedRow[MovementTypeEnum.MECHANIZED] = minorPreparedColumn
            minorPreparedRow[MovementTypeEnum.MOTORIZED] = minorPreparedColumn
            minorPreparedRow[MovementTypeEnum.FOOT] = minorPreparedColumn

            val majorPreparedRow = HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>()

            // By chance the column is same for all movement modes
            val majorPreparedColumn = HashMap<MovementModeEnum, ObstacleTableCell>()
            majorPreparedColumn[MovementModeEnum.COLUM] = ObstacleTableCell(0, 2, true)
            majorPreparedColumn[MovementModeEnum.TACTICAL] = ObstacleTableCell(0, 2, true)
            majorPreparedColumn[MovementModeEnum.DEPLOYED] = ObstacleTableCell(0, 3, true)

            majorPreparedRow[MovementTypeEnum.MECHANIZED] = majorPreparedColumn
            majorPreparedRow[MovementTypeEnum.MOTORIZED] = majorPreparedColumn
            majorPreparedRow[MovementTypeEnum.FOOT] = majorPreparedColumn

            table[ObstacleEnum.MINOR_HASTY] = minorHastyRow
            table[ObstacleEnum.MINOR_PREPARED] = minorPreparedRow
            table[ObstacleEnum.MAJOR_PREPARED] = majorPreparedRow

            return table

        }
    }

    class ObstacleTableCell(val extraMPs : Int, val attritionRollMaxValue : Int?, val causesDelay : Boolean) {}

    fun getAttritionForRiverCrossing(diceResult: DiceRollResult, obstacleEnum: ObstacleEnum, unitState: UnitState) : Int {
        val movementModeTable = Tables.TerrainCombatTable.MovementMode()
        val movementMode = movementModeTable.get(unitState.posture!!)

        val movementType = getMovementType(unitState.unit!!.type)

        val range = getAttritionRange(obstacleEnum, movementType, movementMode) ?: return 0

        if (diceResult.get() <= range) {
            return 1
        }

        return 0
    }

    fun getAttritionForHastyCrossingDuringRetreat(diceResult: DiceRollResult, movementModeEnum : MovementModeEnum): Int {
        // We don't need to know much for this special case
        // I.e. movement type doesn't matter at all, the result is same regardless.
        val range = getAttritionRange(ObstacleEnum.MINOR_HASTY, MovementTypeEnum.MOTORIZED, movementModeEnum) ?: 0
        if (range == 0) {
            return 0
        }

        if (diceResult.get() <= range) {
            return 1
        }

        return 0
    }

    private fun getAttritionRange(obstacleEnum: ObstacleEnum, movementTypeEnum: MovementTypeEnum, movementModeEnum: MovementModeEnum): Int? {
        return obstacleTable.getCell(obstacleEnum, movementTypeEnum, movementModeEnum).attritionRollMaxValue
    }

    fun getRetreatPrerequisites() : String {
        var text = ""

        text += "1. A retreat must be made to an adjacent hex\n\n"
        text += "2. The hex can't a contain breakthrough marker\n\n"
        text += "3. The hex can't be adjacent to enemy blocking units, unless\n A. There's a friendly combat unit in the hex\n B. All blocking enemy units behind an unbridged river or lake\n\n"
        text += "4. Retreating over unbridged major rivers is not possible\n\n"
        text += "5. Retreating over unbridged minor rivers in a hasty crossing is possible for amphibious units, but might result in additional attrition\n\n"

        return text
    }
}