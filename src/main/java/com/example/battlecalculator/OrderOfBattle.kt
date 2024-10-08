package com.example.battlecalculator

import android.util.Log
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
            "alliance_name": "NATO",
            "level_1": [
                {
                    "name": "NL I",
                    "level_2": [
                        {
                            "name": "3rd Panzer Division",
                            "level_3": [
                                {
                                    "name": "7th PzGren Brigade",
                                    "level_4": [
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
                                    "name": "8th PzGren Brigade",
                                    "level_4": [
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
                                    "name": "9th PzGren Brigade",
                                    "level_4": [
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
                            ],
                            "level_4": [
                                {
                                    "name": "3pz_3",
                                    "type": "RECON",
                                    "strength": "3-5",
                                    "cadre": 5
                                },
                                {
                                    "name": "3pz_36",
                                    "type": "MOTORIZED",
                                    "strength": "1-2",
                                    "cadre": 4
                                },
                                {
                                    "name": "3pz_37",
                                    "type": "MOTORIZED",
                                    "strength": "1-2",
                                    "cadre": 4
                                }
                            ]
                        },
                        {
                            "name": "4 PtsInf",
                            "level_3": [
                                {
                                    "name": "4 Pantser",
                                    "level_4": [
                                        {
                                            "name": "nl1_4ptsinf_4pz_43pz",
                                            "type": "ARMOR",
                                            "strength": "6-5",
                                            "cadre": 4
                                        }
                                    ]
                                },
                                {
                                    "name": "41 PtsInf",
                                    "level_4": [
                                        {
                                            "name": "nl1_4ptsinf_41pz_42pi",
                                            "type": "MECHANIZED",
                                            "strength": "6-5",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "nl1_4ptsinf_41pz_41pz",
                                            "type": "ARMOR",
                                            "strength": "6-5",
                                            "cadre": 4
                                        }
                                    ]
                                },
                                {
                                    "name": "NL1 Attached",
                                    "level_4": [
                                        {
                                            "name": "nl1_103r",
                                            "type": "RECON",
                                            "strength": "3-5",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "nl1_25vbp",
                                            "type": "INFANTRY",
                                            "strength": "0-1",
                                            "cadre": 3
                                        }
                                    ]
                                },
                                {
                                    "name": "Generic support",
                                    "level_4": [
                                        {
                                            "name": "generic_support_nato_1",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "generic_support_nato_2",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "generic_support_nato_3",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    "name": "LANDJUT",
                    "level_2": [
                        {
                            "name": "Jutland",
                            "level_3": [
                                {
                                    "name": "1st Brigade",
                                    "level_4": [
                                            {
                                            "name": "1ju_1flr",
                                            "type": "MECHANIZED",
                                            "strength": "3-4",
                                            "cadre": 2
                                        },
                                        {
                                            "name": "1ju_1kjr",
                                            "type": "MECHANIZED",
                                            "strength": "2-4",
                                            "cadre": 2
                                        },
                                        {
                                            "name": "1ju_3jd",
                                            "type": "ARMOR",
                                            "strength": "3-2",
                                            "cadre": 2
                                        }
                                    ]
                                },
                                {
                                    "name": "2nd Brigade",
                                    "level_4": [
                                            {
                                                "name": "2ju_2qlr",
                                                "type": "MECHANIZED",
                                                "strength": "3-4",
                                                "cadre": 2
                                            },
                                            {
                                                "name": "2ju_2jd",
                                                "type": "ARMOR",
                                                "strength": "3-2",
                                                "cadre": 2
                                            },
                                            {
                                                "name": "2ju_1qlr",
                                                "type": "MECHANIZED",
                                                "strength": "2-4",
                                                "cadre": 2
                                            }
                                    ]
                                },
                                {
                                    "name": "3rd Brigade",
                                    "level_4": [
                                            {
                                                "name": "3ju_2sr",
                                                "type": "MECHANIZED",
                                                "strength": "3-4",
                                                "cadre": 2
                                            },
                                            {
                                                "name": "3ju_1sr",
                                                "type": "MECHANIZED",
                                                "strength": "2-4",
                                                "cadre": 2
                                            },
                                            {
                                                "name": "3ju_1jd",
                                                "type": "ARMOR",
                                                "strength": "3-2",
                                                "cadre": 2
                                            }
                                    ]
                                },
                            ],
                            "level_4": [
                                    {
                                        "name": "ju_5jd",
                                        "type": "RECON",
                                        "strength": "2-3",
                                        "cadre": 2
                                    },
                                    {
                                        "name": "lj_5msk",
                                        "type": "INFANTRY",
                                        "strength": "0-2",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "lj_10vbk",
                                        "type": "INFANTRY",
                                        "strength": "0-1",
                                        "cadre": 3
                                    },
                                    {
                                        "name": "51hs_512",
                                        "type": "MECHANIZED",
                                        "strength": "1-3",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "51hs_511",
                                        "type": "MOTORIZED",
                                        "strength": "1-3",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "51hs_514",
                                        "type": "ARMOR",
                                        "strength": "3-2",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "51hs_513",
                                        "type": "ARMOR",
                                        "strength": "3-2",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "71hs_713",
                                        "type": "MOTORIZED",
                                        "strength": "1-2",
                                        "cadre": 3
                                    },
                                    {
                                        "name": "61hs_613",
                                        "type": "ARMOR",
                                        "strength": "3-2",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "61hs_612",
                                        "type": "MOTORIZED",
                                        "strength": "1-3",
                                        "cadre": 4
                                    },
                                    {
                                        "name": "61hs_611",
                                        "type": "MOTORIZED",
                                        "strength": "1-3",
                                        "cadre": 4
                                    }
                            ]
                        },
                        {
                            "name": "6th PzGren Division",
                            "level_3": [
                                {
                                    "name": "16th PzGren Brigade",
                                    "level_4": [
                                            {
                                                "name": "16_164",
                                                "type": "ARMOR",
                                                "strength": "5-4",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "16_161",
                                                "type": "MECHANIZED",
                                                "strength": "4-5",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "16_162",
                                                "type": "MECHANIZED",
                                                "strength": "3-6",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "16_163",
                                                "type": "MECHANIZED",
                                                "strength": "3-6",
                                                "cadre": 5
                                            }
                                    ]
                                },
                                {
                                    "name": "17th PzGren Brigade",
                                    "level_4": [
                                            {
                                                "name": "17_174",
                                                "type": "ARMOR",
                                                "strength": "5-4",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "17_173",
                                                "type": "MECHANIZED",
                                                "strength": "3-6",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "17_172",
                                                "type": "MECHANIZED",
                                                "strength": "3-6",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "17_171",
                                                "type": "MECHANIZED",
                                                "strength": "4-5",
                                                "cadre": 4
                                            }
                                    ]
                                },
                                {
                                    "name": "18th PzGren Brigade",
                                    "level_4": [
                                            {
                                                "name": "18_183",
                                                "type": "ARMOR",
                                                "strength": "5-4",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "18_181",
                                                "type": "ARMOR",
                                                "strength": "4-4",
                                                "cadre": 4
                                            },
                                            {
                                                "name": "18_182",
                                                "type": "MECHANIZED",
                                                "strength": "3-6",
                                                "cadre": 5
                                            },
                                            {
                                                "name": "18_184",
                                                "type": "ARMOR",
                                                "strength": "5-4",
                                                "cadre": 5
                                            }
                                    ]
                                }
                            ],
                            "level_4": [
                                        {
                                            "name": "6pg_66",
                                            "type": "MECHANIZED",
                                            "strength": "1-2",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "6pg_6",
                                            "type": "RECON",
                                            "strength": "3-5",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "6pg_67",
                                            "type": "MECHANIZED",
                                            "strength": "1-2",
                                            "cadre": 4
                                        }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            "alliance_name": "PACT",
            "level_1": [
                {
                    "name": "Northern",
                    "level_2": [
                        {
                            "name": "2nd Guards Tank",
                            "level_3": [
                                {
                                    "name": "94th Guards Motor Rifle Division",
                                    "level_4": [
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
                                    "name": "207th Motor Rifle Division",
                                    "level_4": [
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
                                    "name": "21st Motor Rifle Division",
                                    "level_4": [
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
                                    "level_4": [
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
                                },
                                {
                                    "name": "2GTA attached",
                                    "level_4": [
                                        {
                                            "name": "2gta_138",
                                            "type": "ARMOR",
                                            "strength": "4-2",
                                            "cadre": 4
                                        },
                                        {
                                            "name": "38g_3",
                                            "type": "INFANTRY",
                                            "strength": "2-3",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "38g_2",
                                            "type": "INFANTRY",
                                            "strength": "2-3",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "38g_1",
                                            "type": "INFANTRY",
                                            "strength": "2-3",
                                            "cadre": 5
                                        },
                                        {
                                            "name": "38g_4",
                                            "type": "MECHANIZED",
                                            "strength": "2-1",
                                            "cadre": 5
                                        }
                                    ]
                                },
                                {
                                    "name": "Generic support",
                                    "level_4": [
                                        {
                                            "name": "generic_support_pact_1",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "generic_support_pact_2",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "generic_support_pact_3",
                                            "type": "MECHANIZED",
                                            "strength": "1-1",
                                            "cadre": 3
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name": "5th Army",
                            "level_3": [
                                {
                                    "name": "1st Motor Rifle Division",
                                    "level_4": [
                                        {
                                            "name": "1m_1r",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "1m_1pz",
                                            "type": "ARMOR",
                                            "strength": "6-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "1m_2m",
                                            "type": "RECON",
                                            "strength": "4-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "1m_3m",
                                            "type": "MECHANIZED",
                                            "strength": "5-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "1m_1m",
                                            "type": "RECON",
                                            "strength": "4-7",
                                            "cadre": 3
                                        }
                                    ]
                                },
                                {
                                    "name": "8th Motor Rifle Division",
                                    "level_4": [
                                        {
                                            "name": "8m_8pz",
                                            "type": "ARMOR",
                                            "strength": "5-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "8m_8r",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "8m_28m",
                                            "type": "MECHANIZED",
                                            "strength": "4-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "8m_29m",
                                            "type": "MECHANIZED",
                                            "strength": "5-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "8m_27m",
                                            "type": "MECHANIZED",
                                            "strength": "4-7",
                                            "cadre": 3
                                        }
                                    ]
                                },
                                {
                                    "name": "9th Panzer Rifle Division",
                                    "level_4": [
                                        {
                                            "name": "9pz_22pz",
                                            "type": "ARMOR",
                                            "strength": "7-4",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "9pz_23pz",
                                            "type": "ARMOR",
                                            "strength": "6-4",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "9pz_21pz",
                                            "type": "ARMOR",
                                            "strength": "7-5",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "9pz_9r",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "9pz_9m",
                                            "type": "MECHANIZED",
                                            "strength": "5-8",
                                            "cadre": 3
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "name": "1st Army",
                            "level_3": [
                                {
                                    "name": "12th Mechanized Infantry Division",
                                    "level_4": [
                                        {
                                            "name": "12m_25t",
                                            "type": "ARMOR",
                                            "strength": "5-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "12m_5m",
                                            "type": "MECHANIZED",
                                            "strength": "5-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "12m_41m",
                                            "type": "MECHANIZED",
                                            "strength": "4-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "12m_9m",
                                            "type": "MECHANIZED",
                                            "strength": "4-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "12m_16r",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 3
                                        }
                                    ]
                                },
                                {
                                    "name": "16th Tank Division",
                                    "level_4": [
                                        {
                                            "name": "16t_55m",
                                            "type": "MECHANIZED",
                                            "strength": "5-7",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "16t_1t",
                                            "type": "ARMOR",
                                            "strength": "5-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "16t_58t",
                                            "type": "ARMOR",
                                            "strength": "5-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "16t_14t",
                                            "type": "ARMOR",
                                            "strength": "5-3",
                                            "cadre": 3
                                        },
                                        {
                                            "name": "16t_17r",
                                            "type": "RECON",
                                            "strength": "1-2",
                                            "cadre": 3
                                        }
                                    ]
                                },
                                
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

            val allianceIndex = HashMap<String, Unit>()

            for (level1 in alli.level1) {
                for (level2 in level1.level2) {
                    if (level2.level4 != null) {

                        for (level4 in level2.level4) {
                            val unit = Unit(level4.type, level4.strength, level4.name, level4.cadre)
                            unitIndex[unit.name] = unit
                            allianceIndex[unit.name] = unit
                        }
                    }

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
        val level1: List<Level1>
    )

    data class Level1 (
        val name: String,

        @Json(name = "level_2")
        val level2: List<Level2>,

        @Json(name = "level_4")
        val level4: List<Level4>? = null
    )

    data class Level2 (
        val name: String,

        @Json(name = "level_3")
        val level3: List<Level3>,

        @Json(name = "level_4")
        val level4: List<Level4>? = null
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


