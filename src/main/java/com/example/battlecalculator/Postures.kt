package com.example.battlecalculator


class Postures() {

    private val postureMap = getPostureHashMap()
    private val postureEnumMap = getPostureEnumHashMap()

    fun getAttackPostures() : MutableList<Posture> {
        val attackingPostures = mutableListOf<Posture>()
        for ((_, posture) in postureMap) {
            if (posture.attack != null) {
                attackingPostures.add(posture)
            }
        }

        return attackingPostures
    }

    fun getAll() : MutableList<Posture> {
        val allPostures = mutableListOf<Posture>()
        for ((_, posture) in postureMap) {
            allPostures.add(posture)
        }

        return allPostures
    }

    fun getPostureEnumByStr(str : String) : PostureEnum {
        val trimmed = str.trim()
        return postureEnumMap[trimmed] ?: throw Exception("Couldn't map $str to posture enum")
    }

    fun getPosture(postureEnum: PostureEnum): Posture {

        return postureMap[postureEnum]
            ?: throw Exception("Couldn't find posture $postureEnum from posture map")
    }

    fun getBombardmentModifier(postureEnum: PostureEnum): Int {
        val posture = postureMap[postureEnum]
            ?: throw Exception("Could find data for posture $postureEnum")
        return posture.bombardment
    }

    fun getCombatModifier(postureEnum: PostureEnum, isAttack: Boolean): Int? {
        val posture = postureMap[postureEnum]
        if (isAttack) {
            return posture?.attack
        }

        return posture?.defense
    }

    fun getLeastFavourableDefenseModifier(defenders : MutableList<UnitState>): Pair<Int, String> {
        val defenderPostureEnums = defenders.map { it.posture!! }

        if (defenderPostureEnums.isEmpty()) {
            throw Exception("Defenders size is 0")
        }

        if (defenders.size > 2) {
            throw Exception("There are more than 2 defenders")
        }

        // 6.2.4. Posture transition
        val postureTransitionModifier = 2

        val firstDefenderPosture = defenders[0].posture
        var firstExplanation = "Defender posture (${firstDefenderPosture.toString()}): "

        var firstDefenderValue = postureMap[firstDefenderPosture]!!.defense
        if (defenders[0].inPostureTransition) {
            firstDefenderValue += postureTransitionModifier
            firstExplanation += "$firstDefenderValue including posture transition penalty +2.\n"
        } else {
            firstExplanation += "$firstDefenderValue\n"
        }

        if (defenders.size > 1) {
            val secondDefenderPosture = defenders[1].posture
            var secondExplanation = "Defender posture (${secondDefenderPosture.toString()}): "

            var secondDefenderValue = postureMap[secondDefenderPosture]!!.defense
            if (defenders[1].inPostureTransition) {
                secondDefenderValue += postureTransitionModifier
                secondExplanation += "$secondDefenderValue including posture transition penalty +2.\n"
            } else {
                secondExplanation += "$secondDefenderValue\n"
            }

            if (secondDefenderValue > firstDefenderValue) {
                return Pair(secondDefenderValue, secondExplanation)
            }
        }

        return Pair(firstDefenderValue, firstExplanation)


        /*
        var leastFavourable = -100
        //for (postureEnum in defenderPostureEnums) {
        for ((index, postureEnum) in defenderPostureEnums.withIndex()) {
            var transitionModifier = 0
            if (defenders[index].inPostureTransition) {
                transitionModifier = 2
            }
            val posture = postureMap[postureEnum]
            if (posture!!.defense > leastFavourable) {
                leastFavourable = posture.defense
            }
        }

        return leastFavourable*/
    }

    private fun getPostureEnumHashMap() : HashMap<String, PostureEnum> {
        val postureMap = HashMap<String, PostureEnum>()
        postureMap["Full assault"] = PostureEnum.FASL
        postureMap["March assault"] = PostureEnum.MASL
        postureMap["Refit"] = PostureEnum.REFT
        postureMap["Rigid defense"] = PostureEnum.RDEF
        postureMap["Tactical"] = PostureEnum.TAC
        postureMap["Assault"] = PostureEnum.ASL
        postureMap["Recon"] = PostureEnum.REC
        postureMap["Screen"] = PostureEnum.SCRN
        postureMap["Defense"] = PostureEnum.DEF
        postureMap["Area defense"] = PostureEnum.ADEF
        postureMap["Shoot and scoot"] = PostureEnum.SHOOTSCOOT
        postureMap["Road"] = PostureEnum.ROAD
        postureMap["Barrage"] = PostureEnum.BAR
        postureMap["Close support"] = PostureEnum.CSUP
        postureMap["FARP"] = PostureEnum.FARP
        postureMap["Deployed"] = PostureEnum.DEPL
        postureMap["Moving"] = PostureEnum.MOV
        postureMap["Refit"] = PostureEnum.REFT

        return postureMap
    }

    private fun getPostureHashMap() : HashMap<PostureEnum, Posture> {
        val postureMap = HashMap<PostureEnum, Posture>()
        postureMap[PostureEnum.SCRN] = Posture(-2, -4, 3, PostureEnum.SCRN)
        postureMap[PostureEnum.DEF] = Posture(-1, null, 0, PostureEnum.DEF)
        postureMap[PostureEnum.RDEF] = Posture(-2, null, -2, PostureEnum.RDEF)
        postureMap[PostureEnum.ADEF] = Posture(0, null, 1, PostureEnum.ADEF)
        postureMap[PostureEnum.ASL] = Posture(0, 0, 3, PostureEnum.ASL)
        postureMap[PostureEnum.FASL] = Posture(1, 2, 3, PostureEnum.FASL)
        postureMap[PostureEnum.ROAD] = Posture(4, null, 5, PostureEnum.ROAD)
        postureMap[PostureEnum.TAC] = Posture(0, -3, 2, PostureEnum.TAC)
        postureMap[PostureEnum.REC] = Posture(-2, -4, 3, PostureEnum.REC)
        postureMap[PostureEnum.REFT] = Posture(1, null, 6, PostureEnum.REFT)
        postureMap[PostureEnum.MASL] = Posture(1, -2, 4, PostureEnum.MASL)
        postureMap[PostureEnum.SHOOTSCOOT] = Posture(-1, null, 2, PostureEnum.SHOOTSCOOT)
        postureMap[PostureEnum.ROAD] = Posture(4, null, 5, PostureEnum.ROAD)
        postureMap[PostureEnum.BAR] = Posture(0, null, 3, PostureEnum.BAR)
        postureMap[PostureEnum.CSUP] = Posture(1, null, 4, PostureEnum.CSUP)
        postureMap[PostureEnum.FARP] = Posture(1, null, 2, PostureEnum.FARP)
        postureMap[PostureEnum.DEPL] = Posture(0, null, 0, PostureEnum.DEPL)
        postureMap[PostureEnum.MOV] = Posture(1, null, 5, PostureEnum.MOV)
        postureMap[PostureEnum.REFT] = Posture(1, null, 6, PostureEnum.REFT)

        return postureMap
    }

}

fun getPostureFromString(str : String) : PostureEnum {
    for (enum in PostureEnum.values()) {
        if (str == enum.toString()) {
            return enum
        }
    }

    throw Exception("Couldn't convert $str to posture enum")
}

fun getCommandStateFromString(str : String) : CommandStateEnum {
    for (enum in CommandStateEnum.values()) {
        if (str == enum.toString()) {
            return enum
        }
    }

    throw Exception("Couldn't convert $str to command state enum")
}

fun getAttackTypeFromString(str : String) : AttackTypeEnum {
    for (enum in AttackTypeEnum.values()) {
        if (str == enum.toString()) {
            return enum
        }
    }

    throw Exception("Couldn't convert $str to attack type enum")
}

fun getRiverCrossingTypeFromString(str : String) : RiverCrossingTypeEnum {
    for (enum in RiverCrossingTypeEnum.values()) {
        if (str == enum.toString()) {
            return enum
        }
    }

    throw Exception("Couldn't convert $str to river crossing type enum")
}

fun getEngagementStateFromString(str : String) : EngagementStateEnum {
    for (enum in EngagementStateEnum.values()) {
        if (str == enum.toString()) {
            return enum
        }
    }

    throw Exception("Couldn't convert $str to engagement state enum")
}


class Posture(bombardmentModifier : Int, attackModifier : Int?, defenseModifier : Int, postureEnum: PostureEnum) {

    val bombardment = bombardmentModifier
    val attack = attackModifier
    val defense = defenseModifier
    val enum = postureEnum

}