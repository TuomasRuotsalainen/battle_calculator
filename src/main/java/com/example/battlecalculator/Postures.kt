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

    fun getCombatModifier(postureEnum: PostureEnum, isAttack: Boolean): Int? {
        val posture = postureMap[postureEnum]
        if (isAttack) {
            return posture?.attack
        }

        return posture?.defense
    }

    fun getLeastFavourableDefenseModifier(postureEnums : List<PostureEnum>): Int {
        if (postureEnums.size == 0) {
            throw Exception("Posture enums size is 0")
        }
        var leastFavourable = -100
        for (postureEnum in postureEnums) {
            val posture = postureMap[postureEnum]
            if (posture!!.defense > leastFavourable) {
                leastFavourable = posture.defense
            }
        }

        return leastFavourable
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