package com.example.battlecalculator

class Movement() {

    private val obstacleTable = ObstacleTableRow()

    class ObstacleTableRow() {
        // Here we init the row for hasty crossings. When the whole table is implemented, move this away from this class
        private val contents = init()

        fun getCell(movementTypeEnum: MovementTypeEnum, movementModeEnum: MovementModeEnum) : ObstacleTableCell? {
            return contents[movementTypeEnum]!![movementModeEnum]!!
        }

        private fun init() : HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>> {
            val row = HashMap<MovementTypeEnum, HashMap<MovementModeEnum, ObstacleTableCell>>()

            // By chance the column is same for all movement modes
            val commonColumn = HashMap<MovementModeEnum, ObstacleTableCell>()
            commonColumn[MovementModeEnum.COLUM] = ObstacleTableCell(2, 3, false)
            commonColumn[MovementModeEnum.TACTICAL] = ObstacleTableCell(2, 4, false)
            commonColumn[MovementModeEnum.DEPLOYED] = ObstacleTableCell(3, 5, false)

            row[MovementTypeEnum.MECHANIZED] = commonColumn
            row[MovementTypeEnum.MOTORIZED] = commonColumn
            row[MovementTypeEnum.FOOT] = commonColumn

            return row

        }
    }

    class ObstacleTableCell(val extraMPs : Int, val attritionRollMaxValue : Int?, val causesDelay : Boolean) {}

    fun getAttritionRangeForHastyCrossing(movementTypeEnum: MovementTypeEnum, movementModeEnum: MovementModeEnum) : Int {
        return obstacleTable.getCell(movementTypeEnum,movementModeEnum)!!.attritionRollMaxValue!!
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