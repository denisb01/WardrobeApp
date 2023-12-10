package com.example.myapplication.data

import android.graphics.Bitmap

data class Prediction(
    val type: String,
    val accuracy: Float,
    val bitmap: Bitmap
)
