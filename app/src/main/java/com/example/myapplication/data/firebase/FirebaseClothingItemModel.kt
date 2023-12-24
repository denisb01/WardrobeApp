package com.example.myapplication.data.firebase

import java.io.Serializable

data class FirebaseClothingItemModel(
    val name: String = "",
    var uri: String = "",
    val type: String = "",
    val color: String = "",
    val age: String = "",
    val size: String = "",
    val material: String = "",
    val gender: String = ""
): Serializable
