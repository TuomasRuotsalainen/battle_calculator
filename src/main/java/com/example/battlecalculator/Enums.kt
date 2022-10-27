package com.example.battlecalculator

enum class PostureEnum {
    SCRN, DEF, RDEF, ADEF, ASL, FASL, TAC, REFT, MASL, ROAD, REC, MOV
}

enum class UnitTypeEnum {
    INFANTRY, MOTORIZED, TOWED_ARTILLERY, HELICOPTER, ARMOR, MECHANIZED, OTHER_ARTILLERY, RECON, HQ
}

enum class Alliances {
    NATO, PACT
}

enum class IntentExtraIDs {
    GAMESTATE, UNITSELECTIONTYPE
}

enum class UnitSelectionTypes {
    ATTACKER, DEFENDER
}