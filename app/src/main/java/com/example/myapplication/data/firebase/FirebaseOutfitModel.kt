package com.example.myapplication.data.firebase

import java.io.Serializable

data class FirebaseOutfitModel(
    var name: String = "",
    var type: String = "",
    var season: String = "",
    var occasion: String = "",
    var age: String = "",
    var gender: String = "",

    var headItemID: String? = null,
    var upperBodyItemID: String? = null,
    var longItemID: String? = null,
    var lowerBodyItemID: String? = null,
    var feetItemID: String? = null
): Serializable
