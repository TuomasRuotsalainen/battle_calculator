package com.example.battlecalculator

import org.json.JSONObject
import com.beust.klaxon.*

class Unit(typeStr: String, strengthStr : String, val name: String, cadre : Int) {

    private val ok = checker(strengthStr)

    val type : UnitTypeEnum = UnitTypeEnum.valueOf(typeStr)
    val attack : Int = strengthStr[0].digitToInt()
    val defense : Int = strengthStr[2].digitToInt()
    val cadre : Int = cadre

    fun eatsArmourInCity(): Boolean {
        if (type == UnitTypeEnum.MECHANIZED || type == UnitTypeEnum.MOTORIZED || type == UnitTypeEnum.INFANTRY) {
            return true
        }

        return false
    }

    private fun checker(strengthStr : String): Boolean {
        if (strengthStr.length != 3) {
            throw Exception("strength value of unit $name is not of length 3")
        }

        return true
    }

    fun getString() : String {
        return "$name:\n$type\nAttack: $attack, Defense: $defense\nCadre: $cadre"
    }
}

class OrderOfBattle {

    private val oobJSON = """
    [
        {
            "alliance_name" : "NATO",
            "level_1" : [
                {
                    "name" : "NL I",
                    "level_2" : [
                        {
                            "name" : "3rd Panzer Division",
                            "level_3" : [
                                {
                                    "name" : "7th PzGren Brigade",
                                    "level_4" : [
                                        {
                                            "name": "3pz_7_74",
                                            "type": "ARMOR",
                                            "strength": "5-4",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_7_71",
                                            "type": "MECHANIZED",
                                            "strength": "4-5",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "3pz_7_73",
                                            "type": "MECHANIZED",
                                            "strength": "3-6",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_7_72",
                                            "type": "MECHANIZED",
                                            "strength": "3-6",
                                            "cadre": 5
                                        }
                                    ]
                                },
                                {
                                    "name" : "8th PzGren Brigade",
                                    "level_4" : [
                                        {
                                            "name": "3pz_8_83",
                                            "type": "ARMOR",
                                            "strength": "5-5",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_8_84",
                                            "type": "ARMOR",
                                            "strength": "5-5",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_8_82",
                                            "type": "MECHANIZED",
                                            "strength": "3-6",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_8_81",
                                            "type": "ARMOR",
                                            "strength": "4-5",
                                            "cadre": 4
                                        }
                                    ]
                                },
                                {
                                    "name" : "9th PzGren Brigade",
                                    "level_4" : [
                                        {
                                            "name": "3pz_9_94",
                                            "type": "ARMOR",
                                            "strength": "5-5",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_9_92l",
                                            "type": "MECHANIZED",
                                            "strength": "4-6",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "3pz_9_91",
                                            "type": "ARMOR",
                                            "strength": "4-5",
                                            "cadre": 4
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name" : "4 PtsInf",
                            "level_3" : [
                                {
                                    "name" : "41 Pantser",
                                    "level_4" : [
                                        {
                                            "name" : "145",
                                            "type" : "ARMOR",
                                            "strength" : "4-5"
                                            "cadre" : 3
                                        },
                                        {
                                            "name" : "146",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : 4
                                        }
                                    ]
                                },
                                {
                                    "name" : "42 PtsInf",
                                    "level_4" : [
                                        {
                                            "name" : "345",
                                            "type" : "ARMOR",
                                            "strength" : "4-5"
                                            "cadre" : 4
                                        },
                                        {
                                            "name" : "346",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : 4
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    "name" : "WG I",
                    "level_2" : [
                        {
                            "name" : "7 Panzer",
                            "level_3" : [
                                {
                                    "name" : "19 PzGren",
                                    "level_4" : [
                                        {
                                            "name" : "76",
                                            "type" : "ARMOR",
                                            "strength" : "4-5"
                                            "cadre" : 4
                                        },
                                        {
                                            "name" : "66",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : 4
                                        }
                                    ]
                                },
                                {
                                    "name" : "20 Panzer",
                                    "level_4" : [
                                        {
                                            "name" : "45",
                                            "type" : "ARMOR",
                                            "strength" : "4-5"
                                            "cadre" : 4
                                        },
                                        {
                                            "name" : "46",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : 4
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            "alliance_name" : "PACT",
            "level_1" : [
            {
                    "name" : "Northern",
                    "level_2" : [
                        {
                            "name" : "2nd Guards Tank",
                            "level_3" : [
                                {
                                    "name" : "94th Guards Motor Rifle Division",
                                    "level_4" : [
                                            {
                                                "name": "94gm_74g",
                                                "type": "ARMOR",
                                                "strength": "7-6",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "94gm_204g",
                                                "type": "MECHANIZED",
                                                "strength": "6-8",
                                                "cadre": 4
                                            },
                                                {
                                                "name": "94gm_286g",
                                                "type": "MECHANIZED",
                                                "strength": "5-7",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "94gm_12g",
                                                "type": "RECON",
                                                "strength": "2-2",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "94gm_288g",
                                                "type": "MECHANIZED",
                                                "strength": "5-7",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "94gm_28",
                                                "type": "ARMOR",
                                                "strength": "2-2",
                                                "cadre": 4
                                            }
                                    ]
                                },
                                {
                                    "name" : "207th Motor Rifle Division",
                                    "level_4" : [
                                            {
                                                "name": "207m_33",
                                                "type": "MECHANIZED",
                                                "strength": "5-7",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "207m_41",
                                                "type": "MECHANIZED",
                                                "strength": "6-8",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "207m_16g",
                                                "type": "ARMOR",
                                                "strength": "7-6",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "207m_32",
                                                "type": "ARMOR",
                                                "strength": "2-2",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "207m_400",
                                                "type": "MECHANIZED",
                                                "strength": "6-8",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "207m_6",
                                                "type": "RECON",
                                                "strength": "2-2",
                                                "cadre": 4
                                            }
                                    ]
                                },
                                {
                                    "name" : "21st Motor Rifle Division",
                                    "level_4" : [
                                        {
                                            "name": "21m_18",
                                            "type": "ARMOR",
                                            "strength": "2-2",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "21m_239",
                                            "type": "MECHANIZED",
                                            "strength": "6-8",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "21m_283g",
                                            "type": "MECHANIZED",
                                            "strength": "5-7",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "21m_240",
                                            "type": "MECHANIZED",
                                            "strength": "5-8",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "21m_34",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "21m_33",
                                            "type": "ARMOR",
                                            "strength": "7-6",
                                            "cadre": 4
                                        }
                                    ]
                                },
                                {
                                    "name": "16th Guards Tank Division",
                                    "level_4" : [
                                        {
                                            "name": "16gt_17g",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "16gt_67g",
                                            "type": "ARMOR",
                                            "strength": "8-6",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "16gt_47g",
                                            "type": "ARMOR",
                                            "strength": "8-6",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "16gt_60",
                                            "type": "MECHANIZED",
                                            "strength": "5-8",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "16gt_65g",
                                            "type": "ARMOR",
                                            "strength": "8-6",
                                            "cadre": 4
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
    """


        private val klaxon = Klaxon()

        private val alliances = klaxon.parseArray<OOBData>(oobJSON)!!

        val index = buildUnitIndex()
        val unitIndex = index.first
        val allianceUnitIndex = index.second

    fun getOOB(alliance: Alliances) : OOBData {
        for (element in alliances) {
            if (element.allianceName == alliance.toString()) {
                return element
            }
        }

        throw Exception("No OOB found")
    }

    fun searchUnits(searchTerm: String, alliance: Alliances) : List<Unit> {
        return allianceUnitIndex[alliance]!!.filterKeys { it.contains(searchTerm) }.values.toList()
    }

    private fun buildUnitIndex() : Pair<HashMap<String, Unit>, HashMap<Alliances, HashMap<String, Unit>>> {
        val unitIndex = HashMap<String, Unit>()
        val alliancesUnitIndex = HashMap<Alliances, HashMap<String, Unit>>()

        for (alli in alliances) {
            if (alli.level1 == null) {
                throw Exception("Level 1 of alliance ${alli.allianceName} is null")
            }

            val allianceIndex = HashMap<String, Unit>()

            for (level1 in alli.level1) {
                for (level2 in level1.level2) {
                    for (level3 in level2.level3) {
                        for (level4 in level3.level4) {
                            val unit = Unit(level4.type, level4.strength, level4.name, level4.cadre)
                            unitIndex[unit.name] = unit
                            allianceIndex[unit.name] = unit
                        }
                    }
                }
            }

            val alliance = if (alli.allianceName == "NATO") Alliances.NATO else Alliances.PACT

            alliancesUnitIndex[alliance] = allianceIndex
        }

        return Pair(unitIndex, alliancesUnitIndex)
    }

    /*
    fun getLevel1ByName(oobData: OOBData, name : String): Level1 {
        for (level1 in oobData.level1!!) {
            if (level1.name == name) {
                return level1
            }
        }
        throw Exception("Level 1 not found")

    }

    fun getLevel2ByName(level1 : Level1, level2Name : String): Level2 {
        for (level2 in level1.level2!!) {
            if (level2.name == level2Name) {
                return level2
            }
        }

        throw Exception("Level 2 not found")
    }

    fun getLevel3ByName(level2 : Level2, level3Name : String): Level3 {
        for (level3 in level2.level3!!) {
            if (level3.name == level3Name) {
                return level3
            }
        }

        throw Exception("Level 3 not found")
    }*/

    data class OOBData (
        @Json(name = "alliance_name")
        val allianceName: String,

        @Json(name = "level_1")
        val level1: List<Level1>? = null
    )

    data class Level1 (
        val name: String,

        @Json(name = "level_2")
        val level2: List<Level2>
    )

    data class Level2 (
        val name: String,

        @Json(name = "level_3")
        val level3: List<Level3>
    )

    data class Level3 (
        val name: String,

        @Json(name = "level_4")
        val level4: List<Level4>
    )

    data class Level4 (
        val name: String,
        val type: String,
        val strength: String,
        val cadre: Int
    )

}


