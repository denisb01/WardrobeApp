package com.example.myapplication.navigation

sealed class Screen(val route: String)
{
    object ClothesScreen : Screen("clothes_screen")
    object OutfitsScreen : Screen("outfits_screen")
    object ContactUsScreen : Screen("contact_us_screen")
    object HelpScreen : Screen("help_screen")
}
