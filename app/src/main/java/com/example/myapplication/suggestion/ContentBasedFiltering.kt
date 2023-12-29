package com.example.myapplication.suggestion

import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.data.firebase.FirebaseOutfit
import com.example.myapplication.data.firebase.FirebaseOutfitModel
import com.example.myapplication.data.outfits.LongOutfit
import com.example.myapplication.data.outfits.ShortOutfit
import com.example.myapplication.presentation.app.AppActivity
import kotlin.random.Random

class ContentBasedFiltering() {
    private val featuresCount = AppActivity.ClothingItemColor.values().size + AppActivity.ClothingItemMaterial.values().size

    private val clothingItemType: HashMap<String, String> = hashMapOf(
        "Hat" to "Head",
        "Longsleeve" to "Upper",
        "Shortsleeve" to "Upper",
        "Dress" to "Long",
        "Pants" to "Lower",
        "Shorts" to "Lower",
        "Skirt" to "Lower",
        "Shoes" to "Feet"
    )

    private fun getColorIndex(item: String, offset: Int): Int{
        for (m in AppActivity.ClothingItemColor.values()){
            if(m.color == item)
                return m.index + offset
        }

        return -1
    }

    private fun getMaterialIndex(item: String, offset: Int): Int{
        for (m in AppActivity.ClothingItemMaterial.values()){
            if(m.material == item)
                return m.index + offset
        }

        return -1
    }

    private fun countAppearancesInOutfits(clothingItem: FirebaseClothingItem, outfits: List<FirebaseOutfit>): Int{
        var count = 0

        for (outfit in outfits){
            if (outfit.outfitData.headItemID != null) count += if (outfit.outfitData.headItemID == clothingItem.key) 1 else 0

            count += if (outfit.outfitData.feetItemID == clothingItem.key) 1 else 0

            when(outfit.outfitData.type){
                "Long" -> count += if (outfit.outfitData.longItemID == clothingItem.key) 1 else 0
                "Short" -> {
                    count += if (outfit.outfitData.upperBodyItemID == clothingItem.key) 1 else 0
                    count += if (outfit.outfitData.lowerBodyItemID == clothingItem.key) 1 else 0
                }
            }
        }

        return count
    }


    private fun getAllItemsOfType(clothingItems: List<FirebaseClothingItem>, type: String): MutableList<FirebaseClothingItem>{
        val itemsList = mutableListOf<FirebaseClothingItem>()
        for (item in clothingItems){
            if (clothingItemType[item.clothingItemData.type] == type){
                itemsList.add(item)
            }
        }

        return itemsList
    }

    private fun createFeaturesProfiles(itemsList: List<FirebaseClothingItem>): Array<Array<Int>> {
        val featuresCount = AppActivity.ClothingItemColor.values().size + AppActivity.ClothingItemMaterial.values().size
        val clothingItemsCount = itemsList.size

        val itemsProfiles = Array(clothingItemsCount) { Array(featuresCount) { 0 } }

        for (i in 0 until clothingItemsCount){
            itemsProfiles[i][getColorIndex(itemsList[i].clothingItemData.color, 0)] = 1
            itemsProfiles[i][getMaterialIndex(itemsList[i].clothingItemData.material, AppActivity.ClothingItemColor.values().size)] = 1
        }

        return itemsProfiles
    }

    private fun createWeightedFeatures(itemsProfiles: Array<Array<Int>>, itemFrequencyInOutfits: Array<Int>): Array<Array<Int>> {
        val clothingItemsCount = itemsProfiles.size
        val weightedFeatures = Array(clothingItemsCount) { Array(featuresCount) { 0 } }

        for (i in 0 until clothingItemsCount){
            for (j in 0 until featuresCount)
                weightedFeatures[i][j] = itemFrequencyInOutfits[i] * itemsProfiles[i][j]
        }

        return weightedFeatures
    }

    private fun calculateColumnSums(weightedFeatures: Array<Array<Int>>): Array<Int> {
        val clothingItemsCount = weightedFeatures.size
        val columnSums = Array(featuresCount) { 0 }

        for (i in 0 until featuresCount){
            for (j in 0 until clothingItemsCount)
                columnSums[i] += weightedFeatures[j][i]
        }

        return columnSums
    }

    private fun normalizeValues(array: Array<Int>): Array<Float> {
        var totalValue = 0

        for (value in array) totalValue += value

        val normalizedValues = Array(featuresCount) { 0f }

        for(i in 0 until featuresCount)
            normalizedValues[i] = array[i].toFloat() / totalValue

        return normalizedValues
    }

    private fun applyRandomBias(weightedFeatures: Array<Float>): Array<Float> {
        val min = 0.05f
        val max = 0.2f

        for(i in 0 until weightedFeatures.size){
            weightedFeatures[i] += min + Random.nextFloat() * (max - min)
        }

        return weightedFeatures
    }

    private fun suggestItemByType(normalizedFeaturesWeights: Array<Float>, clothingItems: List<FirebaseClothingItem>, type: String): FirebaseClothingItem {
        val clothingItemsOfType = getAllItemsOfType(clothingItems, type)
        val itemsProfiles = createFeaturesProfiles(clothingItemsOfType)

        val weightedItemsFeatures = Array(itemsProfiles.size) { Array(featuresCount) { 0f } }

        for (i in 0 until itemsProfiles.size){
            for (j in 0 until featuresCount)
                weightedItemsFeatures[i][j] = normalizedFeaturesWeights[j] * itemsProfiles[i][j]
        }

        val weightedFeaturesSums = Array(itemsProfiles.size) { 0f }

        for (i in 0 until  itemsProfiles.size){
            for (j in 0 until featuresCount)
                weightedFeaturesSums[i] += weightedItemsFeatures[i][j]
        }

        val weightedFeaturesSumsWithBias = applyRandomBias(weightedFeaturesSums)

        val maxIdx = weightedFeaturesSumsWithBias.indexOf(weightedFeaturesSumsWithBias.max())

        return clothingItemsOfType[maxIdx]
    }

    fun generateLongOutfit(clothingItems: List<FirebaseClothingItem>, outfits: List<FirebaseOutfit>, outfitData: FirebaseOutfitModel): LongOutfit {
        val filteredOutfits = outfits.filter {
            firebaseOutfit -> firebaseOutfit.outfitData.season ==  outfitData.season
        }.filter {
            firebaseOutfit -> firebaseOutfit.outfitData.occasion ==  outfitData.occasion
        }.filter {
            firebaseOutfit -> firebaseOutfit.outfitData.gender ==  outfitData.gender
        }.filter {
            firebaseOutfit -> firebaseOutfit.outfitData.age ==  outfitData.age
        }

        // Count Appearances of clothing items in outfits
        val itemFrequencyInOutfits = Array(clothingItems.size) { 0 }
        for (i in 0 until itemFrequencyInOutfits.size) itemFrequencyInOutfits[i] = countAppearancesInOutfits(clothingItems[i], filteredOutfits)

        // Create One-Hot encoded clothing item color and material profile
        val itemsProfiles = createFeaturesProfiles(clothingItems)

        // Dot Product of Appearances Count and One-Hot encoded features - for weighted features
        val weightedFeatures = createWeightedFeatures(itemsProfiles, itemFrequencyInOutfits)

        // Count weights for each feature
        val weightedFeaturesSums = calculateColumnSums(weightedFeatures)

        // Normalization
        val normalizedFeaturesWeights = normalizeValues(weightedFeaturesSums)

        // Suggest items
        return LongOutfit(
            headItem = if(outfitData.headItemID != null) suggestItemByType(normalizedFeaturesWeights, clothingItems, "Head") else null,
            longItem = suggestItemByType(normalizedFeaturesWeights, clothingItems, "Long"),
            feetItem = suggestItemByType(normalizedFeaturesWeights, clothingItems, "Feet")
        )
    }

    fun generateShortOutfit(clothingItems: List<FirebaseClothingItem>, outfits: List<FirebaseOutfit>, outfitData: FirebaseOutfitModel): ShortOutfit {
        val filteredOutfits = outfits.filter {
                firebaseOutfit -> firebaseOutfit.outfitData.season ==  outfitData.season
        }.filter {
                firebaseOutfit -> firebaseOutfit.outfitData.occasion ==  outfitData.occasion
        }.filter {
                firebaseOutfit -> firebaseOutfit.outfitData.gender ==  outfitData.gender
        }.filter {
                firebaseOutfit -> firebaseOutfit.outfitData.age ==  outfitData.age
        }

        // Count Appearances of clothing items in outfits
        val itemFrequencyInOutfits = Array(clothingItems.size) { 0 }
        for (i in 0 until itemFrequencyInOutfits.size) itemFrequencyInOutfits[i] = countAppearancesInOutfits(clothingItems[i], filteredOutfits)

        // Create One-Hot encoded clothing item color and material profile
        val itemsProfiles = createFeaturesProfiles(clothingItems)

        // Dot Product of Appearances Count and One-Hot encoded features - for weighted features
        val weightedFeatures = createWeightedFeatures(itemsProfiles, itemFrequencyInOutfits)

        // Count weights for each feature
        val weightedFeaturesSums = calculateColumnSums(weightedFeatures)

        // Normalization
        val normalizedFeaturesWeights = normalizeValues(weightedFeaturesSums)

        // Suggest items
        return ShortOutfit(
            headItem = if(outfitData.headItemID != null) suggestItemByType(normalizedFeaturesWeights, clothingItems, "Head") else null,
            upperBodyItem = suggestItemByType(normalizedFeaturesWeights, clothingItems, "Upper"),
            lowerBodyItem = suggestItemByType(normalizedFeaturesWeights, clothingItems, "Lower"),
            feetItem = suggestItemByType(normalizedFeaturesWeights, clothingItems, "Feet")
        )
    }

}