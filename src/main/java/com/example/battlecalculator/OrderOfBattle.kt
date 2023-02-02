package com.example.battlecalculator

import org.json.JSONObject
import com.beust.klaxon.*

class Unit(typeStr: String, strengthStr : String, val name: String, cadre : String) {

    private val ok = checker(strengthStr)

    val type : UnitTypeEnum = UnitTypeEnum.valueOf(typeStr)
    val attack : Int = strengthStr[0].digitToInt()
    val defense : Int = strengthStr[2].digitToInt()
    val cadre : Int = cadre[0].digitToInt()

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
                            "name" : "1 PtsInf",
                            "level_3" : [
                                {
                                    "name" : "11 PtsInf Brigade",
                                    "level_4" : [
                                        {
                                            "name" : "74",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "cadre" : "4"
                                        },
                                        {
                                            "name" : "73",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "4"
                                        }
                                    ]
                                },
                                {
                                    "name" : "12 PtsInf Brigade",
                                    "level_4" : [
                                        {
                                            "name" : "123",
                                            "type" : "ARMOR",
                                            "strength" : "4-5"
                                            "cadre" : "3"
                                        },
                                        {
                                            "name" : "124",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "3"
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
                                            "cadre" : "3"
                                        },
                                        {
                                            "name" : "146",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "4"
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
                                            "cadre" : "4"
                                        },
                                        {
                                            "name" : "346",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "4"
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
                                            "cadre" : "4"
                                        },
                                        {
                                            "name" : "66",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "4"
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
                                            "cadre" : "4"
                                        },
                                        {
                                            "name" : "46",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "4"
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
                                    "name" : "16th Guards Tank",
                                    "level_4" : [
                                        {
                                            "name" : "74g",
                                            "type" : "ARMOR",
                                            "strength" : "6-3"
                                            "cadre" : "3"
                                        },
                                        {
                                            "name" : "34",
                                            "type" : "MECHANIZED",
                                            "strength" : "8-4"
                                            "cadre" : "3"
                                        }
                                    ]
                                },
                                {
                                    "name" : "21st Mot. Rifle",
                                    "level_4" : [
                                        {
                                            "name" : "45M",
                                            "type" : "MECHANIZED",
                                            "strength" : "3-2"
                                            "cadre" : "3"
                                        },
                                        {
                                            "name" : "67e",
                                            "type" : "ARMOR",
                                            "strength" : "4-7"
                                            "cadre" : "3"
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

        val unitIndex = buildUnitIndex()

    fun getOOB(alliance: Alliances) : OOBData {
        for (element in alliances) {
            if (element.allianceName == alliance.toString()) {
                return element
            }
        }

        throw Exception("No OOB found")
    }

    private fun buildUnitIndex() : HashMap<String, Unit> {
        val unitIndex = HashMap<String, Unit>()
        for (alli in alliances) {
            if (alli.level1 == null) {
                throw Exception("Level 1 of alliance ${alli.allianceName} is null")
            }
            for (level1 in alli.level1) {
                for (level2 in level1.level2) {
                    for (level3 in level2.level3) {
                        for (level4 in level3.level4) {
                            val unit = Unit(level4.type, level4.strength, level4.name, level4.cadre)
                            unitIndex[unit.name] = unit
                        }
                    }
                }
            }
        }

        return unitIndex
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
        val cadre: String
    )

}


