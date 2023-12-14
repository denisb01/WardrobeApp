package com.example.myapplication.data

import java.io.Serializable

data class FirebaseImageModel(
    val name: String = "",
    var uri: String = "",
    val type: String = "",
    val color: String = "",
    val age: String = "",
    val size: String = "",
    val material: String = "",
    val gender: String = ""
): Serializable
