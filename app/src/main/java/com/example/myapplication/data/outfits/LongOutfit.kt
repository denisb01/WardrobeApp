package com.example.myapplication.data.outfits

import com.example.myapplication.data.firebase.FirebaseClothingItem

class LongOutfit(
    var headItem: FirebaseClothingItem?,
    var longItem: FirebaseClothingItem,
    var feetItem: FirebaseClothingItem,
): Outfit