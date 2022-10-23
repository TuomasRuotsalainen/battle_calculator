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
                    "name" : "I Netherlands Corps",
                    "level_2" : [
                        {
                            "name" : "1 PtsInf Division",
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

        private val element = klaxon.parseArray<WelcomeElement>(oobJSON)!!

    fun getOOB() : List<WelcomeElement> {
        return element
    }

    data class WelcomeElement (
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