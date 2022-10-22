package com.example.battlecalculator



class Tables {

    fun disengage(posture: PostureEnum, unitType: UnitTypeEnum) {

        class disengagementResultRangeCell(f1upperLimit: Int, s0lowerLimit: Int) {
            var f1upperLimit: Int = f1upperLimit
            var s0lowerLimit: Int = s0lowerLimit

        }

        // First ROW of cells
        val INF_ADEF_CELL = disengagementResultRangeCell(4, 7)
        val INF_MOV_CELL = disengagementResultRangeCell(4, 8)
        val INF_DEF_CELL = disengagementResultRangeCell(5, 10)
        val INF_OTHER_CELL = disengagementResultRangeCell(6, 11)

        // Second ROW of cells
        val ARM_ADEF_CELL = disengagementResultRangeCell(2, 5)
        val ARM_MOV_CELL = disengagementResultRangeCell(2, 6)
        val ARM_DEF_CELL = disengagementResultRangeCell(3, 8)
        val ARM_OTHER_CELL = disengagementResultRangeCell(4, 9)

        // Third ROW of cells
        val RECON_ADEF_CELL = disengagementResultRangeCell(1, 4)
        val RECON_MOV_CELL = disengagementResultRangeCell(1, 5)
        val RECON_DEF_CELL = disengagementResultRangeCell(2, 7)
        val RECON_OTHER_CELL = disengagementResultRangeCell(3, 8)

        // ADEF_REC_SCR column
        val ADEF_REC_SCR_column : HashMap<UnitTypeEnum,disengagementResultRangeCell> = HashMap<UnitTypeEnum,disengagementResultRangeCell>()

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
        val MOV_TAC_column : HashMap<UnitTypeEnum,disengagementResultRangeCell> = HashMap<UnitTypeEnum,disengagementResultRangeCell>()

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
        val DEF_MASL_column : HashMap<UnitTypeEnum,disengagementResultRangeCell> = HashMap<UnitTypeEnum,disengagementResultRangeCell>()
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
        val OTHER_column : HashMap<UnitTypeEnum,disengagementResultRangeCell> = HashMap<UnitTypeEnum,disengagementResultRangeCell>()
        OTHER_column[UnitTypeEnum.INFANTRY] = INF_OTHER_CELL
        OTHER_column[UnitTypeEnum.MOTORIZED] = INF_OTHER_CELL
        OTHER_column[UnitTypeEnum.TOWED_ARTILLERY] = INF_OTHER_CELL
        OTHER_column[UnitTypeEnum.HELICOPTER] = INF_OTHER_CELL

        OTHER_column[UnitTypeEnum.ARMOR] = ARM_OTHER_CELL
        OTHER_column[UnitTypeEnum.MECHANIZED] = ARM_OTHER_CELL
        OTHER_column[UnitTypeEnum.OTHER_ARTILLERY] = ARM_OTHER_CELL
        OTHER_column[UnitTypeEnum.HQ] = ARM_OTHER_CELL

        OTHER_column[UnitTypeEnum.RECON] = RECON_OTHER_CELL

        val disengagementTable:HashMap<PostureEnum,HashMap<UnitTypeEnum,disengagementResultRangeCell>> = HashMap<PostureEnum,HashMap<UnitTypeEnum,disengagementResultRangeCell>>()

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


    }

}