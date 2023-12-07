package com.example.myapplication.data

data class Prediction(
    val label: String,
    val accuracy: Float,
    val position: FloatArray
)
