package com.example.myapplication.data.outfits

import com.example.myapplication.data.firebase.FirebaseClothingItem

class ShortOutfit(
    var headItem: FirebaseClothingItem?,
    var upperBodyItem: FirebaseClothingItem,
    var lowerBodyItem: FirebaseClothingItem,
    var feetItem: FirebaseClothingItem
): Outfit