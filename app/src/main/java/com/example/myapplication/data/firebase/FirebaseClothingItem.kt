package com.example.myapplication.data.firebase

import java.io.Serializable

data class FirebaseClothingItem(
    val key: String,
    val clothingItemData: FirebaseClothingItemModel
) : Serializable
