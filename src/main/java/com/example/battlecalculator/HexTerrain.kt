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