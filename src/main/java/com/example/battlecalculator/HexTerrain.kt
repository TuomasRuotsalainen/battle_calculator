package com.example.battlecalculator

enum class TerrainEnum {
    PLAIN, FOREST, TOWN, CITY, SWAMP, MINORRIVER, MAJORRIVER, DEFENSE1, DEFENSE3, BRIDGE
}

class HexTerrain(terrainFeatures : MutableList<TerrainEnum>) {

    val features : HashMap<TerrainEnum, Boolean> = convertToHashMap(terrainFeatures)

    fun isPlainsOnly() : Boolean {
        if (features[TerrainEnum.PLAIN] == true) {
            if (features[TerrainEnum.TOWN] == false && features[TerrainEnum.CITY] == false && features[TerrainEnum.FOREST] == false && features[TerrainEnum.SWAMP] == false) {
                return true
            }
        }

        return false
    }

    fun getFeatureForBombardment() : TerrainEnum {
        for (feature in features) {
            if (feature.value) {
                return feature.key
            }
        }

        throw Exception("No features available")
    }

    fun getDefensiveWorksCombatModifier(): Int {
        val defWorks1 = this.features[TerrainEnum.DEFENSE1]
        val defWorks3 = this.features[TerrainEnum.DEFENSE3]

        if (defWorks1 != null) {
            if (defWorks1) {
                return -1
            }
        }

        if (defWorks3 != null) {
            if (defWorks3) {
                return -3
            }
        }

        return 0
    }

    private fun contains(terrainEnum: TerrainEnum): Boolean {
        return features[terrainEnum]
            ?: return false
    }

    fun getObstacle(riverCrossingTypeEnum: RiverCrossingTypeEnum) : ObstacleEnum? {
        if (contains(TerrainEnum.BRIDGE)) {
            return if (contains(TerrainEnum.MINORRIVER)) {
                ObstacleEnum.MINOR_BRIDGED
            } else if(contains(TerrainEnum.MAJORRIVER)) {
                ObstacleEnum.MAJOR_BRIDGED
            } else {
                throw Exception("There is a bridge but no river")
            }
        } else {
            if (contains(TerrainEnum.MINORRIVER)) {
                return when (riverCrossingTypeEnum) {
                    RiverCrossingTypeEnum.HASTY -> {
                        ObstacleEnum.MINOR_HASTY
                    }
                    RiverCrossingTypeEnum.PREPARED -> {
                        ObstacleEnum.MINOR_PREPARED
                    }
                    else -> {
                        throw Exception("Crossing minor river with no crossing type")
                    }
                }
            } else if (contains(TerrainEnum.MAJORRIVER)) {
                // Case major river
                if (riverCrossingTypeEnum == RiverCrossingTypeEnum.PREPARED) {
                    return ObstacleEnum.MAJOR_PREPARED
                } else {
                    throw Exception("Attempting to cross major river without preparation")
                }
            } else {
                return null
            }
        }
    }

    private fun convertToHashMap(terrainFeatures : MutableList<TerrainEnum>) : HashMap<TerrainEnum, Boolean> {
        val featureMap = HashMap<TerrainEnum, Boolean>()

        for (terrainFeature in terrainFeatures) {
            featureMap[terrainFeature] = true
        }

        validateFeatures(featureMap)

        return featureMap

    }

    private fun validateFeatures(terrainFeatures : HashMap<TerrainEnum, Boolean>) {
        if (terrainFeatures.size == 0) {
            throw Exception("Validate hex features: length is 0")
        }

        if (terrainFeatures[TerrainEnum.MAJORRIVER] != null && terrainFeatures[TerrainEnum.MINORRIVER] != null) {
            throw Exception("Validate hex features: major and minor river can't be present at the same time")
        }

        if (terrainFeatures[TerrainEnum.MAJORRIVER] == null && terrainFeatures[TerrainEnum.MINORRIVER] == null && terrainFeatures[TerrainEnum.BRIDGE] != null) {
            throw Exception("Validate hex features: bridge exists but no rivers")
        }

        if (terrainFeatures[TerrainEnum.DEFENSE1] != null && terrainFeatures[TerrainEnum.DEFENSE3] != null) {
            throw Exception("Validate hex features: defenses 1 and 3 can't be present at the same time")
        }
    }
}