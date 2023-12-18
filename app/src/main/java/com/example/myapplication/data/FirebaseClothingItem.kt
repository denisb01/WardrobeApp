package com.example.myapplication.data

import java.io.Serializable

data class FirebaseClothingItem(
    val key: String,
    val clothingItemData: FirebaseClothingItemModel
) : Serializable
