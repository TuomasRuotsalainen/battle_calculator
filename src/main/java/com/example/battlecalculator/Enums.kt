package com.example.battlecalculator

enum class PostureEnum {
    SCRN, DEF, RDEF, ADEF, ASL, FASL, TAC, REFT, MASL, ROAD, REC, MOV, BAR, SHOOTSCOOT, DEPL, FARP, CSUP
}

enum class PostureWords {
    screen, defense, rigid_defense, area_defense, assault, full_assault, tactical, refit, march_assault, road, recon, moving, barrage, shoot_and_scoot, deployed, farp, close_support
}

enum class MovementModeEnum {
    COLUM, TACTICAL, DEPLOYED, FIXED
}

enum class MovementTypeEnum {
    MECHANIZED, MOTORIZED, FOOT
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
    ATTACKER, DEFENDER, BOMBARDMENT
}

enum class FixedModifierEnum {
    NATO_DEFENDS_MULTI_COUNTRY, DEFENDER_RESTING, DEFENDER_ENGAGED, DEFENDER_HALF_ENGAGED, DEFENDER_DELAYED, DEFENDER_OUT_OF_COMMAND_SCREEN_REC, DEFENDER_OUT_OF_COMMAND, DEFENDER_FRONT_LINE_COMMAND, PACT_ATTACKING_REAR, PACT_DEFENDING_REAR, ATTACKER_HALF_ENGAGED, ATTACKER_OUT_OF_COMMAND, ATTACKER_OUT_OF_COMMAND_SCREEN_REC, ATTACKER_FRONT_LINE_COMMAND, ATTACKER_USES_REC, ATTACKER_USES_SAPPERS, ATTACKER_HAS_SAPPERS, DEFENDER_USES_SAPPERS, DEFENDER_HAS_SAPPERS
}

enum class CommandStateEnum {
    NORMAL, FRONT_LINE_COMMAND, OUT_OF_COMMAND
}

enum class EngagementStateEnum {
    NONE, HALF_ENGAGED, ENGAGED
}

enum class EwEffectEnum {
    ENEMY_AVIATION_HALVED, ENEMY_ARTILLERY_HALVED, ENEMY_COMMAND_DISRUPTED
}

enum class HourEnum {
    H00, H03, H06, H09, H12, H15, H18, H21
}

enum class DayEnum {
    D0, D1, D2, D3, D4
}

enum class DisengagemenResult {
    F1, S1, S0
}
enum class ObstacleEnum {
    MINOR_HASTY, MINOR_PREPARED, MINOR_BRIDGED, MAJOR_PREPARED, MAJOR_BRIDGED, RIBBON
}

enum class DetectionLevel {
    COMBAT_UNIT_ADJACENT, SUPPORT_UNIT_ADJACENT, COMBAT_UNIT_WITHIN_4, SUPPORT_UNIT_WITHIN_4, COMBAT_UNIT_OTHER, SUPPORT_UNIT_OTHER
}

enum class DetectionModifiers {
    ROAD, BARR_CSUP_MASL, ARTILLERY_WITHIN_2, MOVING_HQ, SPOTTED_HQ, FARP, CITY
}
