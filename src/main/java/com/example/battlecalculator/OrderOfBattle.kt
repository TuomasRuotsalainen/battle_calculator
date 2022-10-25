package com.example.battlecalculator

import org.json.JSONObject
import com.beust.klaxon.*

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
                                            "name" : "12 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "13 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
                                        }
                                    ]
                                },
                                {
                                    "name" : "12 PtsInf Brigade",
                                    "level_4" : [
                                        {
                                            "name" : "2 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "3 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
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
                                            "name" : "14 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "12 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
                                        }
                                    ]
                                },
                                {
                                    "name" : "42 PtsInf",
                                    "level_4" : [
                                        {
                                            "name" : "22 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "32 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
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
                                            "name" : "12 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "13 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
                                        }
                                    ]
                                },
                                {
                                    "name" : "20 Panzer",
                                    "level_4" : [
                                        {
                                            "name" : "2 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-5",
                                            "image" : "foobar"
                                        },
                                        {
                                            "name" : "3 Battalion",
                                            "type" : "ARMOR",
                                            "strength" : "4-7",
                                            "image" : "foobar2"
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
            "alliance_name" : "PACT"
        }
    ]
    """


        private val klaxon = Klaxon()

        private val elements = klaxon.parseArray<OOBData>(oobJSON)!!

    fun getOOBs() : List<OOBData> {
        return elements
    }

    fun getOOB(alliance: Alliances) : OOBData {
        for (element in elements) {
            if (element.allianceName == alliance.toString()) {
                return element
            }
        }

        throw Exception("No OOB found")
    }

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
    }

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
        val image: String
    )


}